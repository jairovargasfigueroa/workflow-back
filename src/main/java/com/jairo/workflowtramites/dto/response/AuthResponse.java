package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.enums.Rol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private String id;
    private String email;
    private String nombre;
    private Rol rol;
    private String departamentoId;
}
