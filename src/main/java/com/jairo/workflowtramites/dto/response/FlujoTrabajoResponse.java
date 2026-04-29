package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.enums.EstadoFlujo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FlujoTrabajoResponse {

    private String id;
    private String nombre;
    private String descripcion;
    private String procesoKey;
    private EstadoFlujo estadoFlujo;
    private boolean tieneBorrador;
    private LocalDateTime borradorActualizacion;
    private Integer versionActualNumero;
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
