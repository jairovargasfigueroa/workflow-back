package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.request.FlujoTrabajoRequest;
import com.jairo.workflowtramites.dto.response.FlujoTrabajoResponse;
import com.jairo.workflowtramites.model.FlujoTrabajo;
import com.jairo.workflowtramites.model.enums.EstadoFlujo;

public class FlujoTrabajoMapper {

    public static FlujoTrabajoResponse toResponse(FlujoTrabajo f) {
        return FlujoTrabajoResponse.builder()
                .id(f.getId())
                .nombre(f.getNombre())
                .descripcion(f.getDescripcion())
                .procesoKey(f.getProcesoKey())
                .estadoFlujo(f.getEstadoFlujo())
                .tieneBorrador(f.getXmlBorrador() != null && !f.getXmlBorrador().isBlank())
                .borradorActualizacion(f.getBorradorActualizacion())
                .versionActualNumero(f.getVersionActualNumero())
                .creadoPor(f.getCreadoPor())
                .fechaCreacion(f.getFechaCreacion())
                .fechaActualizacion(f.getFechaActualizacion())
                .build();
    }

    public static FlujoTrabajo toModel(FlujoTrabajoRequest r, String procesoKey) {
        return FlujoTrabajo.builder()
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .procesoKey(procesoKey)
                .estadoFlujo(EstadoFlujo.SIN_PUBLICAR)
                .build();
    }
}
