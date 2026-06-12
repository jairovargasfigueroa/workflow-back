package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.embeds.RespuestaCampo;
import com.jairo.workflowtramites.model.embeds.RespuestaDepartamento;
import com.jairo.workflowtramites.model.enums.EstadoSla;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SolicitudTramiteResponse {

    private String id;
    private String tramiteId;
    private String tramiteNombre;
    private String solicitanteId;
    private String solicitanteNombre;
    private EstadoTramite estado;
    private List<String> departamentosActuales;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaFinalizacion;
    private List<RespuestaCampo> respuestasSolicitante;
    private List<RespuestaDepartamento> respuestasPorDepartamento;

    // SLA del trámite a nivel de solicitud
    private LocalDateTime fechaLimite;
    private EstadoSla estadoSla;
}
