package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.response.EventoAuditoriaResponse;
import com.jairo.workflowtramites.model.EventoAuditoriaArchivo;

public final class EventoAuditoriaMapper {

    private EventoAuditoriaMapper() {}

    public static EventoAuditoriaResponse toResponse(EventoAuditoriaArchivo e) {
        if (e == null) return null;
        return EventoAuditoriaResponse.builder()
                .id(e.getId())
                .archivoId(e.getArchivoId())
                .solicitudId(e.getSolicitudId())
                .usuarioId(e.getUsuarioId())
                .usuarioNombre(e.getUsuarioNombre())
                .usuarioRol(e.getUsuarioRol())
                .usuarioDepartamentoId(e.getUsuarioDepartamentoId())
                .tipo(e.getTipo() != null ? e.getTipo().name() : null)
                .fecha(e.getFecha())
                .detalle(e.getDetalle())
                .motivoDenegacion(e.getMotivoDenegacion())
                .build();
    }
}
