package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.enums.EstadoTramite;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SolicitudTramiteResumen {

    private String id;
    private String tramiteId;
    private String tramiteNombre;
    private String solicitanteId;
    private String solicitanteNombre;
    private EstadoTramite estado;
    private List<String> departamentosActuales;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFinalizacion;
    private LocalDateTime fechaEntradaDepartamentoActual;
}
