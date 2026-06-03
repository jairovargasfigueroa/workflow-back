package com.jairo.workflowtramites.dto.response.reportes;

import com.jairo.workflowtramites.model.enums.EstadoTramite;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SolicitudReporteResponse {

    private String id;
    private String tramiteId;
    private String tramiteNombre;
    private String solicitanteId;
    private String solicitanteNombre;
    private EstadoTramite estado;
    private List<String> departamentosActuales;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFinalizacion;
    private Double horasTranscurridas;
}
