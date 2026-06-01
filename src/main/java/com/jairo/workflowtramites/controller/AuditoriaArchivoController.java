package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.response.EventoAuditoriaResponse;
import com.jairo.workflowtramites.mapper.EventoAuditoriaMapper;
import com.jairo.workflowtramites.model.enums.TipoEventoArchivo;
import com.jairo.workflowtramites.service.AuditoriaArchivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auditoria/archivos")
@RequiredArgsConstructor
public class AuditoriaArchivoController {

    private final AuditoriaArchivoService auditoriaService;

    @GetMapping
    public Map<String, Object> buscar(
            @RequestParam(required = false) String archivoId,
            @RequestParam(required = false) String solicitudId,
            @RequestParam(required = false) String usuarioId,
            @RequestParam(required = false) TipoEventoArchivo tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Page<?> resultado = auditoriaService.buscar(archivoId, solicitudId, usuarioId, tipo, desde, hasta, page, size);

        var contenido = resultado.getContent().stream()
                .map(e -> EventoAuditoriaMapper.toResponse((com.jairo.workflowtramites.model.EventoAuditoriaArchivo) e))
                .toList();

        return Map.of(
                "contenido", contenido,
                "totalElementos", resultado.getTotalElements(),
                "totalPaginas", resultado.getTotalPages(),
                "paginaActual", resultado.getNumber()
        );
    }
}
