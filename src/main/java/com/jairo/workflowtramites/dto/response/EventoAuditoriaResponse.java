package com.jairo.workflowtramites.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoAuditoriaResponse {

    private String id;
    private String archivoId;
    private String solicitudId;
    private String usuarioId;
    private String usuarioNombre;
    private String usuarioRol;
    private String usuarioDepartamentoId;
    private String tipo;
    private LocalDateTime fecha;
    private String detalle;
    private String motivoDenegacion;
}
