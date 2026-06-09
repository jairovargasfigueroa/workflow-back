package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.request.SolicitudTramiteRequest;
import com.jairo.workflowtramites.dto.response.SolicitudTramiteResumen;
import com.jairo.workflowtramites.dto.response.SolicitudTramiteResponse;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.embeds.RespuestaDepartamento;
import com.jairo.workflowtramites.model.enums.EstadoTramite;

import java.util.ArrayList;

public class SolicitudTramiteMapper {

    public static SolicitudTramiteResponse toResponse(SolicitudTramite s) {
        return SolicitudTramiteResponse.builder()
                .id(s.getId())
                .tramiteId(s.getTramiteId())
                .tramiteNombre(s.getTramiteNombre())
                .solicitanteId(s.getSolicitanteId())
                .solicitanteNombre(s.getSolicitanteNombre())
                .estado(s.getEstado())
                .departamentosActuales(s.getDepartamentosActuales())
                .fechaCreacion(s.getFechaCreacion())
                .fechaActualizacion(s.getFechaActualizacion())
                .fechaFinalizacion(s.getFechaFinalizacion())
                .respuestasSolicitante(s.getRespuestasSolicitante())
                .respuestasPorDepartamento(s.getRespuestasPorDepartamento())
                .adjuntos(s.getAdjuntos())
                .fechaLimite(s.getFechaLimite())
                .estadoSla(s.getEstadoSla())
                .build();
    }

    public static SolicitudTramiteResumen toResumen(SolicitudTramite s) {
        return SolicitudTramiteResumen.builder()
                .id(s.getId())
                .tramiteId(s.getTramiteId())
                .tramiteNombre(s.getTramiteNombre())
                .solicitanteId(s.getSolicitanteId())
                .solicitanteNombre(s.getSolicitanteNombre())
                .estado(s.getEstado())
                .departamentosActuales(s.getDepartamentosActuales())
                .fechaCreacion(s.getFechaCreacion())
                .fechaFinalizacion(s.getFechaFinalizacion())
                .fechaLimite(s.getFechaLimite())
                .estadoSla(s.getEstadoSla())
                .build();
    }

    public static SolicitudTramiteResumen toResumenParaDepartamento(SolicitudTramite s, String departamentoId) {
        SolicitudTramiteResumen resumen = toResumen(s);
        s.getRespuestasPorDepartamento().stream()
                .filter(r -> departamentoId.equals(r.getDepartamentoId()) && r.getFechaRespuesta() == null)
                .map(RespuestaDepartamento::getFechaEntrada)
                .findFirst()
                .ifPresent(resumen::setFechaEntradaDepartamentoActual);
        return resumen;
    }

    public static SolicitudTramite toModel(SolicitudTramiteRequest r, String solicitanteId) {
        return SolicitudTramite.builder()
                .tramiteId(r.getTramiteId())
                .solicitanteId(solicitanteId)
                .estado(EstadoTramite.PENDIENTE)
                .respuestasSolicitante(r.getRespuestas() != null ? r.getRespuestas() : new ArrayList<>())
                .respuestasPorDepartamento(new ArrayList<>())
                .adjuntos(r.getAdjuntos() != null ? new ArrayList<>(r.getAdjuntos()) : new ArrayList<>())
                .build();
    }
}
