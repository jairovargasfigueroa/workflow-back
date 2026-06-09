package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.response.OnlyOfficeEditorConfigResponse;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import com.jairo.workflowtramites.model.Archivo;
import com.jairo.workflowtramites.model.Usuario;
import com.jairo.workflowtramites.model.enums.AccionArchivo;
import com.jairo.workflowtramites.model.enums.TipoEventoArchivo;
import com.jairo.workflowtramites.repository.ArchivoRepository;
import com.jairo.workflowtramites.repository.UsuarioRepository;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import com.jairo.workflowtramites.service.permission.PermisoArchivoService;
import com.jairo.workflowtramites.service.storage.StorageService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnlyOfficeService {

    private final ArchivoRepository archivoRepository;
    private final PermisoArchivoService permisoArchivoService;
    private final StorageService storageService;
    private final ArchivoService archivoService;
    private final AuditoriaArchivoService auditoriaService;
    private final UsuarioRepository usuarioRepository;

    @Value("${onlyoffice.document-server-url}")
    private String documentServerUrl;

    @Value("${onlyoffice.jwt-secret}")
    private String jwtSecret;

    @Value("${onlyoffice.callback-base-url}")
    private String callbackBaseUrl;

    @Value("${onlyoffice.token-expiration-minutes}")
    private int tokenExpirationMinutes;

    // Formatos que se pueden EDITAR colaborativamente en OnlyOffice.
    private static final Set<String> FORMATOS_EDITABLES = Set.of("docx", "xlsx", "pptx");

    // Formatos que OnlyOffice puede MOSTRAR (incluye los editables + PDF y otros de solo lectura).
    // Imagenes/video NO van aca (los muestra el navegador); zip/binarios solo se descargan.
    private static final Set<String> FORMATOS_VISUALIZABLES = Set.of(
            "docx", "xlsx", "pptx", "doc", "xls", "ppt",
            "odt", "ods", "odp", "pdf", "txt", "csv", "rtf"
    );
    private static final String DOWNLOAD_CLAIM_KEY = "archivoId";
    private static final String DOWNLOAD_CLAIM_PURPOSE = "purpose";
    private static final String DOWNLOAD_PURPOSE_VALUE = "onlyoffice-download";

    public OnlyOfficeEditorConfigResponse abrirEditor(String archivoId, boolean soloVista) {
        Archivo archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Archivo no encontrado: " + archivoId));

        if (!FORMATOS_VISUALIZABLES.contains(archivo.getFormato())) {
            throw new RuntimeException("Formato no soportado por el editor: " + archivo.getFormato());
        }

        AuthenticatedUser usuario = usuarioActual();
        if (!permisoArchivoService.puede(usuario, AccionArchivo.VER, archivo)) {
            throw new AccessDeniedException("No tienes permiso para abrir este archivo");
        }

        // Solo abre en EDIT si: no es solo-vista, el formato es editable, no es inmutable y tiene permiso.
        // En cualquier otro caso (PDF, solo lectura, sin permiso) abre en VIEW.
        boolean puedeEditar = !soloVista
                && FORMATOS_EDITABLES.contains(archivo.getFormato())
                && !archivo.isInmutable()
                && permisoArchivoService.puede(usuario, AccionArchivo.SUBIR_VERSION, archivo);

        // Registra el acceso al documento (aqui SI hay usuario en sesion: viene del JWT del front).
        auditoriaService.registrar(TipoEventoArchivo.VIEW_METADATA, archivo.getId(), archivo.getSolicitudId(),
                (puedeEditar ? "Abrió para editar" : "Abrió para ver") + " en el editor: " + archivo.getNombre());

        String tokenDescarga = generarTokenDescarga(archivo.getId());
        String urlDescarga = callbackBaseUrl
                + "/api/onlyoffice/contenido/" + archivo.getId()
                + "?token=" + tokenDescarga;

        String documentKey = archivo.getId() + "-v" + archivo.getVersion();

        Map<String, Object> document = new LinkedHashMap<>();
        document.put("fileType", archivo.getFormato());
        document.put("key", documentKey);
        document.put("title", archivo.getNombre());
        document.put("url", urlDescarga);
        document.put("permissions", Map.of(
                "edit", puedeEditar,
                "download", true,
                "print", true
        ));

        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", usuario.getId());
        user.put("name", usuario.getNombre());

        Map<String, Object> editorConfig = new LinkedHashMap<>();
        editorConfig.put("mode", puedeEditar ? "edit" : "view");
        editorConfig.put("callbackUrl", callbackBaseUrl + "/api/onlyoffice/callback?archivoId=" + archivo.getId());
        editorConfig.put("user", user);
        editorConfig.put("lang", "es");

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("documentType", documentType(archivo.getFormato()));
        config.put("document", document);
        config.put("editorConfig", editorConfig);
        config.put("type", "desktop");

        String token = generarJwt(config);
        config.put("token", token);

        return OnlyOfficeEditorConfigResponse.builder()
                .documentServerUrl(documentServerUrl)
                .config(config)
                .token(token)
                .build();
    }

    public ContenidoArchivo obtenerContenido(String archivoId, String token) {
        validarTokenDescarga(token, archivoId);

        Archivo archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Archivo no encontrado: " + archivoId));

        if (archivo.getEstado() == Archivo.EstadoArchivo.ELIMINADO) {
            throw new RecursoNoEncontradoException("Archivo eliminado: " + archivoId);
        }

        InputStream contenido = storageService.descargar(archivo.getS3Key());

        return new ContenidoArchivo(
                contenido,
                archivo.getNombre(),
                archivo.getContentType(),
                archivo.getTamanoBytes());
    }

    private String generarTokenDescarga(String archivoId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim(DOWNLOAD_CLAIM_KEY, archivoId)
                .claim(DOWNLOAD_CLAIM_PURPOSE, DOWNLOAD_PURPOSE_VALUE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMinutes * 60_000L))
                .signWith(key)
                .compact();
    }

    private void validarTokenDescarga(String token, String archivoIdEsperado) {
        if (token == null || token.isBlank()) {
            throw new AccessDeniedException("Token de descarga ausente");
        }
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String purpose = claims.get(DOWNLOAD_CLAIM_PURPOSE, String.class);
            String archivoIdEnToken = claims.get(DOWNLOAD_CLAIM_KEY, String.class);

            if (!DOWNLOAD_PURPOSE_VALUE.equals(purpose)) {
                throw new AccessDeniedException("Token con propósito inválido");
            }
            if (!archivoIdEsperado.equals(archivoIdEnToken)) {
                throw new AccessDeniedException("Token no corresponde al archivo solicitado");
            }
        } catch (JwtException e) {
            throw new AccessDeniedException("Token de descarga inválido o expirado");
        }
    }

    public record ContenidoArchivo(InputStream stream, String nombre, String contentType, Long tamanoBytes) {}

    public Map<String, Object> procesarCallback(String archivoId, Map<String, Object> payload) {
        // Con JWT habilitado, OnlyOffice envia los datos del callback dentro de un token
        // firmado (no planos). Lo desempacamos y validamos con el secreto compartido;
        // esto ademas evita que un tercero falsifique el callback y reemplace archivos.
        Map<String, Object> datos = desempacarCallback(payload);

        Object statusObj = datos.get("status");
        Integer status = (statusObj instanceof Number n) ? n.intValue() : null;
        if (status == null) return Map.of("error", 0);

        // status 2 = listo para guardar (todos cerraron), 6 = guardado forzado mientras editan
        if (status == 2 || status == 6) {
            String url = (String) datos.get("url");
            if (url != null) {
                descargarYGuardarNuevaVersion(archivoId, url, datos);
            }
        }

        return Map.of("error", 0);
    }

    /**
     * Devuelve los datos reales del callback. Si OnlyOffice tiene JWT habilitado, el
     * payload trae un campo "token" (JWT firmado con el secreto compartido) cuyos claims
     * son el callback real (status, url, users...). Si no hay token (JWT off), se usa el
     * payload plano. Un token presente pero invalido se rechaza (callback no confiable).
     */
    private Map<String, Object> desempacarCallback(Map<String, Object> payload) {
        Object token = payload.get("token");
        if (token == null || token.toString().isBlank()) {
            return payload;
        }
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token.toString())
                    .getPayload();
            return new HashMap<>(claims);
        } catch (JwtException e) {
            log.warn("Callback de OnlyOffice con token invalido o no firmado por nosotros");
            throw new AccessDeniedException("Callback de OnlyOffice no autorizado");
        }
    }

    private void descargarYGuardarNuevaVersion(String archivoId, String url, Map<String, Object> payload) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            byte[] bytes = response.body().readAllBytes();

            Archivo actual = archivoRepository.findById(archivoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Archivo no encontrado en callback: " + archivoId));

            // Defensa en profundidad: un archivo inmutable NO debe reemplazarse, ni siquiera
            // por un callback (no deberia llegar aqui porque se abre en modo view, pero por si acaso).
            if (actual.isInmutable()) {
                log.warn("Callback de OnlyOffice para archivo inmutable {}, no se guarda", archivoId);
                return;
            }

            String nuevoArchivoId = UUID.randomUUID().toString();
            String nuevoS3Key = construirS3KeyVersionada(actual, nuevoArchivoId);

            storageService.subir(nuevoS3Key, new java.io.ByteArrayInputStream(bytes), bytes.length, actual.getContentType());

            actual.setEstado(Archivo.EstadoArchivo.REEMPLAZADO);
            actual.setFechaModificacion(LocalDateTime.now());
            archivoRepository.save(actual);

            String editorNombre = resolverNombreEditor(payload);

            Archivo nueva = Archivo.builder()
                    .id(nuevoArchivoId)
                    .solicitudId(actual.getSolicitudId())
                    .politicaId(actual.getPoliticaId())
                    .clienteId(actual.getClienteId())
                    .nombre(actual.getNombre())
                    .formato(actual.getFormato())
                    .tamanoBytes((long) bytes.length)
                    .contentType(actual.getContentType())
                    .s3Key(nuevoS3Key)
                    .subidoPor(actual.getSubidoPor())
                    .subidoPorNombre(editorNombre != null ? editorNombre : actual.getSubidoPorNombre())
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

            archivoRepository.save(nueva);

            registrarAuditoriaEdicion(nueva, payload);
        } catch (IOException | InterruptedException e) {
            log.error("Error procesando callback de OnlyOffice", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Registra en la auditoría a TODOS los funcionarios que participaron en la sesión
     * de edición colaborativa. OnlyOffice manda en "users" la lista de IDs que editaron;
     * resolvemos cada uno a su nombre real. Si no viene la lista, registra un evento genérico.
     */
    private void registrarAuditoriaEdicion(Archivo archivo, Map<String, Object> payload) {
        Object users = payload.get("users");
        String detalle = "Edición colaborativa (OnlyOffice) de " + archivo.getNombre();

        if (users instanceof java.util.List<?> list && !list.isEmpty()) {
            for (Object u : list) {
                if (u == null) continue;
                String userId = u.toString();
                String nombre = usuarioRepository.findById(userId)
                        .map(Usuario::getNombre)
                        .orElse(userId);
                auditoriaService.registrarConUsuario(TipoEventoArchivo.NEW_VERSION,
                        archivo.getId(), archivo.getSolicitudId(), detalle, userId, nombre);
            }
        } else {
            auditoriaService.registrarConUsuario(TipoEventoArchivo.NEW_VERSION,
                    archivo.getId(), archivo.getSolicitudId(), detalle, null, "OnlyOffice");
        }
    }

    private String construirS3KeyVersionada(Archivo actual, String nuevoArchivoId) {
        return String.format("politica-%s/cliente-%s/tramite-%s/%s-%s",
                actual.getPoliticaId(),
                actual.getClienteId(),
                actual.getSolicitudId(),
                nuevoArchivoId,
                actual.getNombre());
    }

    /**
     * Resuelve el nombre real del primer editor de la sesión (para mostrarlo como autor
     * de la nueva versión). OnlyOffice manda IDs en "users"; los traducimos a nombre.
     */
    private String resolverNombreEditor(Map<String, Object> payload) {
        Object users = payload.get("users");
        if (users instanceof java.util.List<?> list && !list.isEmpty() && list.get(0) != null) {
            String userId = list.get(0).toString();
            return usuarioRepository.findById(userId).map(Usuario::getNombre).orElse(userId);
        }
        return null;
    }

    private String documentType(String formato) {
        return switch (formato) {
            case "docx", "doc", "odt", "txt", "rtf" -> "word";
            case "xlsx", "xls", "ods", "csv" -> "cell";
            case "pptx", "ppt", "odp" -> "slide";
            // OnlyOffice abre PDF con documentType "word" (visor). "pdf" no es valido como documentType.
            case "pdf" -> "word";
            default -> "word";
        };
    }

    private String generarJwt(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claimsMap = new HashMap<>(claims);
        return Jwts.builder()
                .claims(claimsMap)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMinutes * 60_000L))
                .signWith(key)
                .compact();
    }

    private AuthenticatedUser usuarioActual() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AuthenticatedUser u)) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return u;
    }
}
