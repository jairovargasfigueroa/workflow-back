package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.request.UsuarioRequest;
import com.jairo.workflowtramites.dto.response.UsuarioResponse;
import com.jairo.workflowtramites.model.Usuario;

public class UsuarioMapper {

    public static UsuarioResponse toResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .rol(u.getRol())
                .activo(u.isActivo())
                .fechaCreacion(u.getFechaCreacion())
                .departamentoId(u.getDepartamentoId())
                .telefono(u.getTelefono())
                .direccion(u.getDireccion())
                .cedula(u.getCedula())
                .build();
    }

    public static Usuario toModel(UsuarioRequest r) {
        return Usuario.builder()
                .nombre(r.getNombre())
                .email(r.getEmail())
                .password(r.getPassword())
                .rol(r.getRol())
                .activo(true)
                .departamentoId(r.getDepartamentoId())
                .telefono(r.getTelefono())
                .direccion(r.getDireccion())
                .cedula(r.getCedula())
                .build();
    }
}
