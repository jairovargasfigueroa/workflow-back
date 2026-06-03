package com.jairo.workflowtramites.dto.response.reportes;

import com.jairo.workflowtramites.model.enums.EstadoTramite;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DemoraResponse {

    private String solicitudId;
    private String tramiteNombre;
    private String solicitanteNombre;
    private String departamentoActualId;
    private String departamentoActualNombre;
    private EstadoTramite estado;
    private LocalDateTime fechaCreacion;
    private double horasTranscurridas;
}
