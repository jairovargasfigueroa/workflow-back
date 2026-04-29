package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.request.TramiteRequest;
import com.jairo.workflowtramites.dto.response.TramiteResponse;
import com.jairo.workflowtramites.model.Tramite;

import java.util.ArrayList;

public class TramiteMapper {

    public static TramiteResponse toResponse(Tramite t) {
        return TramiteResponse.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .formularioSolicitanteId(t.getFormularioSolicitanteId())
                .flujoTrabajoId(t.getFlujoTrabajoId())
                .requisitos(t.getRequisitos())
                .activo(t.isActivo())
                .fechaCreacion(t.getFechaCreacion())
                .build();
    }

    public static Tramite toModel(TramiteRequest r) {
        return Tramite.builder()
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .formularioSolicitanteId(r.getFormularioSolicitanteId())
                .flujoTrabajoId(r.getFlujoTrabajoId())
                .requisitos(r.getRequisitos() != null ? r.getRequisitos() : new ArrayList<>())
                .activo(true)
                .build();
    }
}
