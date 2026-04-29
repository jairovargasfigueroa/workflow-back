package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.request.FormularioTemplateRequest;
import com.jairo.workflowtramites.dto.response.FormularioTemplateResponse;
import com.jairo.workflowtramites.model.FormularioTemplate;

import java.util.ArrayList;

public class FormularioTemplateMapper {

    public static FormularioTemplateResponse toResponse(FormularioTemplate f) {
        return FormularioTemplateResponse.builder()
                .id(f.getId())
                .titulo(f.getTitulo())
                .descripcion(f.getDescripcion())
                .campos(f.getCampos() != null ? f.getCampos() : new ArrayList<>())
                .activo(f.isActivo())
                .fechaCreacion(f.getFechaCreacion())
                .build();
    }

    public static FormularioTemplate toModel(FormularioTemplateRequest r) {
        return FormularioTemplate.builder()
                .titulo(r.getTitulo())
                .descripcion(r.getDescripcion())
                .campos(r.getCampos() != null ? r.getCampos() : new ArrayList<>())
                .activo(true)
                .build();
    }
}
