package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.response.OnlyOfficeEditorConfigResponse;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import com.jairo.workflowtramites.model.Archivo;
import com.jairo.workflowtramites.model.enums.AccionArchivo;
import com.jairo.workflowtramites.repository.ArchivoRepository;
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

    @Value("${onlyoffice.document-server-url}")
    private String documentServerUrl;

    @Value("${onlyoffice.jwt-secret}")
    private String jwtSecret;

    @Value("${onlyoffice.callback-base-url}")
    private String callbackBaseUrl;

    @Value("${onlyoffice.token-expiration-minutes}")
    private int tokenExpirationMinutes;

    private static final Set<String> FORMATOS_EDITABLES = Set.of("docx", "xlsx", "pptx");
    private static final String DOWNLOAD_CLAIM_KEY = "archivoId";
    private static final String DOWNLOAD_CLAIM_PURPOSE = "purpose";
    private static final String DOWNLOAD_PURPOSE_VALUE = "onlyoffice-download";

    public OnlyOfficeEditorConfigResponse abrirEditor(String archivoId) {
        Archivo archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Archivo no encontrado: " + archivoId));

        if (!FORMATOS_EDITABLES.contains(archivo.getFormato())) {
            throw new RuntimeException("Formato no editable colaborativamente: " + archivo.getFormato());
        }

        AuthenticatedUser usuario = usuarioActual();
        if (!permisoArchivoService.puede(usuario, AccionArchivo.VER, archivo)) {
            throw new AccessDeniedException("No tienes permiso para abrir este archivo");
        }

        boolean puedeEditar = !archivo.isInmutable()
                && permisoArchivoService.puede(usuario, AccionArchivo.SUBIR_VERSION, archivo);

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
        Integer status = (Integer) payload.get("status");
        if (status == null) return Map.of("error", 0);

        if (status == 2 || status == 6) {
            String url = (String) payload.get("url");
            if (url != null) {
                descargarYGuardarNuevaVersion(archivoId, url, payload);
            }
        }

        return Map.of("error", 0);
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

            String nuevoArchivoId = UUID.randomUUID().toString();
            String nuevoS3Key = construirS3KeyVersionada(actual, nuevoArchivoId);

            storageService.subir(nuevoS3Key, new java.io.ByteArrayInputStream(bytes), bytes.length, actual.getContentType());

            actual.setEstado(Archivo.EstadoArchivo.REEMPLAZADO);
            actual.setFechaModificacion(LocalDateTime.now());
            archivoRepository.save(actual);

            String editorNombre = extraerNombreEditor(payload);

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
                    .subidoPorNombre(editorNombre != null ? editorNombre + " (OnlyOffice)" : actual.getSubidoPorNombre())
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
        } catch (IOException | InterruptedException e) {
            log.error("Error procesando callback de OnlyOffice", e);
            Thread.currentThread().interrupt();
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

    @SuppressWarnings("unchecked")
    private String extraerNombreEditor(Map<String, Object> payload) {
        Object users = payload.get("users");
        if (users instanceof java.util.List<?> list && !list.isEmpty()) {
            return list.get(0).toString();
        }
        return null;
    }

    private String documentType(String formato) {
        return switch (formato) {
            case "docx", "doc" -> "word";
            case "xlsx", "xls" -> "cell";
            case "pptx", "ppt" -> "slide";
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
