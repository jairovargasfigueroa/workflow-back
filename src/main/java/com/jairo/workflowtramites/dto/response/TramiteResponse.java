package com.jairo.workflowtramites.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TramiteResponse {

    private String id;
    private String nombre;
    private String descripcion;
    private String formularioSolicitanteId;
    private String flujoTrabajoId;
    private List<String> requisitos;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}
