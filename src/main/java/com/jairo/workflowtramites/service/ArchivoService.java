package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.response.ArchivoDescargaResponse;
import com.jairo.workflowtramites.dto.response.reportes.PaginaResponse;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import com.jairo.workflowtramites.model.Archivo;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.VersionFlujo;
import com.jairo.workflowtramites.model.embeds.ConfiguracionDocumental;
import com.jairo.workflowtramites.model.embeds.DocumentoConfig;
import com.jairo.workflowtramites.model.embeds.NodoFlujo;
import com.jairo.workflowtramites.model.embeds.PermisoSet;
import com.jairo.workflowtramites.model.enums.AccionArchivo;
import com.jairo.workflowtramites.repository.ArchivoRepository;
import com.jairo.workflowtramites.repository.SolicitudTramiteRepository;
import com.jairo.workflowtramites.repository.VersionFlujoRepository;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import com.jairo.workflowtramites.service.permission.PermisoArchivoService;
import com.jairo.workflowtramites.service.storage.StorageService;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchivoService {

    private final ArchivoRepository archivoRepository;
    private final SolicitudTramiteRepository solicitudRepository;
    private final VersionFlujoRepository versionFlujoRepository;
    private final StorageService storageService;
    private final PermisoArchivoService permisoArchivoService;

    @Value("${aws.s3.presigned-url-expiration-minutes}")
    private int presignedUrlExpirationMinutes;

    // El docente pidió soportar "cualquier formato" (Word, Excel, PDF, imágenes, video, etc.).
    // En lugar de una lista blanca restrictiva, bloqueamos solo ejecutables/scripts por seguridad
    // (los archivos van a S3, no se ejecutan en el servidor, pero evitamos distribuir binarios
    // peligrosos desde el repositorio documental). Todo lo demás se acepta.
    private static final Set<String> FORMATOS_BLOQUEADOS = Set.of(
            "exe", "bat", "cmd", "com", "scr", "msi", "sh", "bash",
            "ps1", "vbs", "jar", "dll", "app", "deb", "dmg"
    );

    public Archivo subir(MultipartFile archivo,
                         String solicitudId,
                         String campoFormulario,
                         String departamentoOrigenId,
                         String clientId) {
        // Idempotencia: si el cliente (movil offline) reintenta con el mismo clientId,
        // devolvemos el archivo ya subido en vez de duplicarlo.
        if (clientId != null && !clientId.isBlank()) {
            var existente = archivoRepository.findFirstByClientId(clientId);
            if (existente.isPresent()) {
                return existente.get();
            }
        }
        validarArchivo(archivo);

        SolicitudTramite solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada: " + solicitudId));

        AuthenticatedUser usuario = usuarioActual();

        // Control server-side de quién puede SUBIR a este slot (el front además oculta el botón).
        NodoFlujo nodo = encontrarNodo(solicitud, departamentoOrigenId, campoFormulario);
        DocumentoConfig docConfig = encontrarDocumentoConfig(nodo, campoFormulario);
        if (!permisoArchivoService.puedeSubir(usuario, docConfig, solicitud)) {
            throw new RuntimeException("No tiene permiso para subir este documento");
        }

        String nombreOriginal = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "archivo";
        String formato = extraerExtension(nombreOriginal);
        String archivoId = UUID.randomUUID().toString();
        String s3Key = construirS3Key(solicitud, archivoId, nombreOriginal);

        try {
            storageService.subir(s3Key, archivo.getInputStream(), archivo.getSize(), archivo.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo a S3: " + e.getMessage(), e);
        }

        PermisoSet permisos = derivarPermisos(nodo, docConfig);

        Archivo nuevo = Archivo.builder()
                .id(archivoId)
                .clientId(clientId)
                .solicitudId(solicitudId)
                .politicaId(solicitud.getTramiteId())
                .clienteId(solicitud.getSolicitanteId())
                .nombre(nombreOriginal)
                .formato(formato)
                .tamanoBytes(archivo.getSize())
                .contentType(archivo.getContentType())
                .s3Key(s3Key)
                .subidoPor(usuario.getId())
                .subidoPorNombre(usuario.getNombre())
                .fechaSubida(LocalDateTime.now())
                .departamentoOrigenId(departamentoOrigenId)
                .campoFormularioOrigen(campoFormulario)
                .nodoElementId(nodo != null ? nodo.getElementId() : null)
                .version(1)
                .linajeId(archivoId)
                .estado(Archivo.EstadoArchivo.ACTIVO)
                .permisos(permisos)
                .build();

        return archivoRepository.save(nuevo);
    }

    private NodoFlujo encontrarNodo(SolicitudTramite solicitud, String departamentoOrigenId, String campoFormulario) {
        if (solicitud.getVersionFlujoId() == null) return null;
        Optional<VersionFlujo> opt = versionFlujoRepository.findById(solicitud.getVersionFlujoId());
        if (opt.isEmpty()) return null;
        List<NodoFlujo> nodos = opt.get().getNodos();
        if (nodos == null) return null;

        if (departamentoOrigenId != null) {
            for (NodoFlujo n : nodos) {
                if (departamentoOrigenId.equals(n.getDepartamentoId())) return n;
            }
        }

        if (campoFormulario != null) {
            for (NodoFlujo n : nodos) {
                if (n.getConfiguracionDocumental() == null) continue;
                if (matchPorCampo(n.getConfiguracionDocumental().getDocumentosProducidos(), campoFormulario)) return n;
            }
        }

        return null;
    }

    private boolean matchPorCampo(List<DocumentoConfig> docs, String identificador) {
        if (docs == null) return false;
        // El documento producido se identifica por su nombre (el front lo manda como campoFormulario).
        return docs.stream().anyMatch(d -> identificador.equals(d.getNombre()));
    }

    private DocumentoConfig encontrarDocumentoConfig(NodoFlujo nodo, String campoFormulario) {
        if (nodo == null || nodo.getConfiguracionDocumental() == null) return null;
        if (campoFormulario == null) return null;
        ConfiguracionDocumental config = nodo.getConfiguracionDocumental();
        if (config.getDocumentosProducidos() == null) return null;

        return config.getDocumentosProducidos().stream()
                .filter(d -> campoFormulario.equals(d.getNombre()))
                .findFirst()
                .orElse(null);
    }

    private PermisoSet derivarPermisos(NodoFlujo nodo, DocumentoConfig docConfig) {
        if (docConfig != null && docConfig.getPermisos() != null) {
            return docConfig.getPermisos();
        }
        if (nodo != null && nodo.getConfiguracionDocumental() != null) {
            PermisoSet adHoc = nodo.getConfiguracionDocumental().getPermisosDefaultAdHoc();
            if (adHoc != null) return adHoc;
        }
        return PermisoSet.builder().build();
    }

    public List<Archivo> listarPorSolicitud(String solicitudId) {
        AuthenticatedUser usuario = usuarioActual();
        return archivoRepository.findBySolicitudIdAndEstadoNot(solicitudId, Archivo.EstadoArchivo.ELIMINADO)
                .stream()
                .filter(a -> permisoArchivoService.puede(usuario, AccionArchivo.VER, a))
                .toList();
    }

    /**
     * Repositorio documental global (vista del admin para "hacer consultas").
     * Lista la metadata desde MongoDB (NO recorre S3) de los archivos ACTIVOS,
     * aplicando filtros opcionales + permisos del usuario, ordenados por fecha desc
     * y paginados. S3 solo se toca al descargar.
     */
    public PaginaResponse<Archivo> listarRepositorio(
            String tramiteId, String clienteId, String formato,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            int page, int size) {

        AuthenticatedUser usuario = usuarioActual();
        int tamano = Math.min(Math.max(size, 1), 200);
        int pagina = Math.max(page, 0);

        List<Archivo> filtrados = archivoRepository.findByEstado(Archivo.EstadoArchivo.ACTIVO)
                .stream()
                .filter(a -> tramiteId == null || tramiteId.equals(a.getPoliticaId()))
                .filter(a -> clienteId == null || clienteId.equals(a.getClienteId()))
                .filter(a -> formato == null || formato.equalsIgnoreCase(a.getFormato()))
                .filter(a -> fechaDesde == null
                        || (a.getFechaSubida() != null && !a.getFechaSubida().isBefore(fechaDesde)))
                .filter(a -> fechaHasta == null
                        || (a.getFechaSubida() != null && !a.getFechaSubida().isAfter(fechaHasta)))
                .filter(a -> permisoArchivoService.puede(usuario, AccionArchivo.VER, a))
                .sorted(Comparator.comparing(Archivo::getFechaSubida,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        long total = filtrados.size();
        int totalPaginas = (int) Math.ceil((double) total / tamano);

        List<Archivo> contenido = filtrados.stream()
                .skip((long) pagina * tamano)
                .limit(tamano)
                .toList();

        return PaginaResponse.<Archivo>builder()
                .contenido(contenido)
                .pagina(pagina)
                .tamano(tamano)
                .total(total)
                .totalPaginas(totalPaginas)
                .build();
    }

    public Archivo obtenerPorId(String archivoId) {
        return archivoRepository.findById(archivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Archivo no encontrado: " + archivoId));
    }

    public Archivo obtenerMetadata(String archivoId) {
        Archivo archivo = obtenerPorId(archivoId);
        if (!permisoArchivoService.puede(usuarioActual(), AccionArchivo.VER, archivo)) {
            throw new AccessDeniedException("No tienes permiso para ver este archivo");
        }
        return archivo;
    }

    public ArchivoDescargaResponse generarUrlDescarga(String archivoId, boolean attachment) {
        Archivo archivo = obtenerPorId(archivoId);

        if (archivo.getEstado() == Archivo.EstadoArchivo.ELIMINADO) {
            throw new RecursoNoEncontradoException("Archivo eliminado: " + archivoId);
        }

        if (!permisoArchivoService.puede(usuarioActual(), AccionArchivo.DESCARGAR, archivo)) {
            throw new AccessDeniedException("No tienes permiso para descargar este archivo");
        }

        Duration duracion = Duration.ofMinutes(presignedUrlExpirationMinutes);
        // attachment=true -> el navegador BAJA el archivo; false -> lo MUESTRA inline (imagen/video/pdf).
        String tipo = attachment ? "attachment" : "inline";
        String nombreCodificado = URLEncoder.encode(archivo.getNombre(), StandardCharsets.UTF_8).replace("+", "%20");
        String contentDisposition = tipo + "; filename*=UTF-8''" + nombreCodificado;
        String url = storageService.generarUrlPrefirmadaDescarga(archivo.getS3Key(), duracion, contentDisposition);

        return ArchivoDescargaResponse.builder()
                .archivoId(archivo.getId())
                .nombre(archivo.getNombre())
                .contentType(archivo.getContentType())
                .urlDescarga(url)
                .expiraEn(LocalDateTime.now().plusMinutes(presignedUrlExpirationMinutes))
                .build();
    }

    public Archivo subirNuevaVersion(String archivoId, MultipartFile nuevoArchivo) {
        validarArchivo(nuevoArchivo);

        Archivo actual = obtenerPorId(archivoId);

        if (actual.getEstado() == Archivo.EstadoArchivo.ELIMINADO) {
            throw new RecursoNoEncontradoException("No se puede versionar un archivo eliminado");
        }

        AuthenticatedUser usuario = usuarioActual();

        if (!permisoArchivoService.puede(usuario, AccionArchivo.SUBIR_VERSION, actual)) {
            throw new AccessDeniedException("No tienes permiso para subir nueva versión de este archivo");
        }

        SolicitudTramite solicitud = solicitudRepository.findById(actual.getSolicitudId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));

        String nombreOriginal = nuevoArchivo.getOriginalFilename() != null ? nuevoArchivo.getOriginalFilename() : actual.getNombre();
        String formato = extraerExtension(nombreOriginal);
        String nuevoArchivoId = UUID.randomUUID().toString();
        String nuevoS3Key = construirS3Key(solicitud, nuevoArchivoId, nombreOriginal);

        try {
            storageService.subir(nuevoS3Key, nuevoArchivo.getInputStream(), nuevoArchivo.getSize(), nuevoArchivo.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Error al subir nueva versión a S3: " + e.getMessage(), e);
        }

        actual.setEstado(Archivo.EstadoArchivo.REEMPLAZADO);
        actual.setModificadoPor(usuario.getId());
        actual.setFechaModificacion(LocalDateTime.now());
        archivoRepository.save(actual);

        Archivo nuevaVersion = Archivo.builder()
                .id(nuevoArchivoId)
                .solicitudId(actual.getSolicitudId())
                .politicaId(actual.getPoliticaId())
                .clienteId(actual.getClienteId())
                .nombre(nombreOriginal)
                .formato(formato)
                .tamanoBytes(nuevoArchivo.getSize())
                .contentType(nuevoArchivo.getContentType())
                .s3Key(nuevoS3Key)
                .subidoPor(usuario.getId())
                .subidoPorNombre(usuario.getNombre())
                .fechaSubida(LocalDateTime.now())
                .departamentoOrigenId(actual.getDepartamentoOrigenId())
                .campoFormularioOrigen(actual.getCampoFormularioOrigen())
                .nodoElementId(actual.getNodoElementId())
                .version(actual.getVersion() + 1)
                .versionAnteriorId(actual.getId())
                .linajeId(actual.getLinajeId() != null ? actual.getLinajeId() : actual.getId())
                .estado(Archivo.EstadoArchivo.ACTIVO)
                .permisos(actual.getPermisos())
                .inmutable(actual.isInmutable())
                .build();

        return archivoRepository.save(nuevaVersion);
    }

    public List<Archivo> listarVersiones(String archivoId) {
        Archivo archivo = obtenerPorId(archivoId);
        if (!permisoArchivoService.puede(usuarioActual(), AccionArchivo.VER, archivo)) {
            throw new AccessDeniedException("No tienes permiso para ver este archivo");
        }
        String linaje = archivo.getLinajeId() != null ? archivo.getLinajeId() : archivo.getId();
        return archivoRepository.findByLinajeIdOrderByVersionAsc(linaje);
    }

    public Archivo revertirAVersion(String archivoId, int numeroVersion) {
        Archivo archivo = obtenerPorId(archivoId);
        AuthenticatedUser usuario = usuarioActual();
        if (!permisoArchivoService.puede(usuario, AccionArchivo.SUBIR_VERSION, archivo)) {
            throw new AccessDeniedException("No tienes permiso para revertir versiones de este archivo");
        }
        if (archivo.isInmutable()) {
            throw new AccessDeniedException("Este archivo es inmutable, no se puede revertir");
        }

        String linaje = archivo.getLinajeId() != null ? archivo.getLinajeId() : archivo.getId();
        List<Archivo> versiones = archivoRepository.findByLinajeIdOrderByVersionAsc(linaje);

        Archivo objetivo = versiones.stream()
                .filter(v -> v.getVersion() == numeroVersion)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión no encontrada: " + numeroVersion));

        Archivo actualActiva = versiones.stream()
                .filter(v -> v.getEstado() == Archivo.EstadoArchivo.ACTIVO)
                .findFirst()
                .orElse(archivo);

        if (actualActiva.getVersion().equals(numeroVersion)) {
            return actualActiva;
        }

        int maxVersion = versiones.stream().mapToInt(Archivo::getVersion).max().orElse(actualActiva.getVersion());

        actualActiva.setEstado(Archivo.EstadoArchivo.REEMPLAZADO);
        actualActiva.setModificadoPor(usuario.getId());
        actualActiva.setFechaModificacion(LocalDateTime.now());
        archivoRepository.save(actualActiva);

        Archivo copia = Archivo.builder()
                .id(UUID.randomUUID().toString())
                .solicitudId(objetivo.getSolicitudId())
                .politicaId(objetivo.getPoliticaId())
                .clienteId(objetivo.getClienteId())
                .nombre(objetivo.getNombre())
                .formato(objetivo.getFormato())
                .tamanoBytes(objetivo.getTamanoBytes())
                .contentType(objetivo.getContentType())
                .s3Key(objetivo.getS3Key())
                .subidoPor(usuario.getId())
                .subidoPorNombre(usuario.getNombre())
                .fechaSubida(LocalDateTime.now())
                .departamentoOrigenId(objetivo.getDepartamentoOrigenId())
                .campoFormularioOrigen(objetivo.getCampoFormularioOrigen())
                .nodoElementId(objetivo.getNodoElementId())
                .version(maxVersion + 1)
                .versionAnteriorId(actualActiva.getId())
                .linajeId(linaje)
                .estado(Archivo.EstadoArchivo.ACTIVO)
                .permisos(objetivo.getPermisos())
                .inmutable(objetivo.isInmutable())
                .build();

        return archivoRepository.save(copia);
    }

    public void eliminar(String archivoId) {
        Archivo archivo = obtenerPorId(archivoId);

        if (archivo.getEstado() == Archivo.EstadoArchivo.ELIMINADO) {
            return;
        }

        AuthenticatedUser usuario = usuarioActual();

        if (!permisoArchivoService.puede(usuario, AccionArchivo.ELIMINAR, archivo)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este archivo");
        }

        // SOFT DELETE: NO borramos de S3. En un sistema de gestion documental el documento se
        // preserva (trazabilidad/auditoria: "registro completo", "el contrato nadie lo toca").
        // Solo se marca ELIMINADO: deja de aparecer en los listados (que filtran por estado) y
        // no se puede descargar ni versionar. El archivo fisico queda en S3 por si hay que auditar.
        archivo.setEstado(Archivo.EstadoArchivo.ELIMINADO);
        archivo.setEliminadoPor(usuario.getId());
        archivo.setFechaEliminacion(LocalDateTime.now());
        archivoRepository.save(archivo);
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }
        String nombre = archivo.getOriginalFilename();
        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre del archivo es inválido");
        }
        String formato = extraerExtension(nombre);
        if (FORMATOS_BLOQUEADOS.contains(formato)) {
            throw new RuntimeException("Formato no permitido por seguridad: " + formato);
        }
    }

    private String extraerExtension(String nombre) {
        int idx = nombre.lastIndexOf('.');
        if (idx == -1 || idx == nombre.length() - 1) return "";
        return nombre.substring(idx + 1).toLowerCase();
    }

    private String construirS3Key(SolicitudTramite solicitud, String archivoId, String nombreOriginal) {
        return String.format("politica-%s/cliente-%s/tramite-%s/%s-%s",
                solicitud.getTramiteId(),
                solicitud.getSolicitanteId(),
                solicitud.getId(),
                archivoId,
                nombreOriginal);
    }

    private AuthenticatedUser usuarioActual() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AuthenticatedUser user)) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return user;
    }
}
