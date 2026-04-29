package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.enums.Rol;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponse {

    private String id;
    private String nombre;
    private String email;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private String departamentoId;
    private String telefono;
    private String direccion;
    private String cedula;
}
