package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.request.DepartamentoRequest;
import com.jairo.workflowtramites.dto.response.DepartamentoResponse;
import com.jairo.workflowtramites.model.Departamento;

public class DepartamentoMapper {

    public static DepartamentoResponse toResponse(Departamento d) {
        return DepartamentoResponse.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .activo(d.isActivo())
                .fechaCreacion(d.getFechaCreacion())
                .build();
    }

    public static Departamento toModel(DepartamentoRequest r) {
        return Departamento.builder()
                .nombre(r.getNombre())
                .activo(r.isActivo())
                .build();
    }
}
