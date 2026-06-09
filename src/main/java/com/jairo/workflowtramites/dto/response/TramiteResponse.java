package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.enums.CategoriaCriticidad;
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
    private List<String> etiquetas;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    // SLA del trámite
    private Integer plazoObjetivoHoras;
    private Integer plazoMaximoHoras;
    private Integer umbralAlertaPorcentaje;
    private CategoriaCriticidad criticidad;
}
