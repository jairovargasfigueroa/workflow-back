package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.response.OnlyOfficeEditorConfigResponse;
import com.jairo.workflowtramites.service.OnlyOfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/onlyoffice")
@RequiredArgsConstructor
public class OnlyOfficeController {

    private final OnlyOfficeService onlyOfficeService;

    @GetMapping("/abrir/{archivoId}")
    public OnlyOfficeEditorConfigResponse abrir(@PathVariable String archivoId) {
        return onlyOfficeService.abrirEditor(archivoId);
    }

    @GetMapping("/contenido/{archivoId}")
    public ResponseEntity<InputStreamResource> contenido(@PathVariable String archivoId,
                                                          @RequestParam String token) {
        OnlyOfficeService.ContenidoArchivo contenido = onlyOfficeService.obtenerContenido(archivoId, token);

        String nombreCodificado = URLEncoder.encode(contenido.nombre(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename*=UTF-8''" + nombreCodificado);
        if (contenido.tamanoBytes() != null) {
            headers.setContentLength(contenido.tamanoBytes());
        }

        MediaType mediaType = contenido.contentType() != null
                ? MediaType.parseMediaType(contenido.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(new InputStreamResource(contenido.stream()));
    }

    @PostMapping("/callback")
    public Map<String, Object> callback(@RequestParam String archivoId,
                                        @RequestBody Map<String, Object> payload) {
        return onlyOfficeService.procesarCallback(archivoId, payload);
    }
}
