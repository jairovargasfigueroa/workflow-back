package com.jairo.workflowtramites.dto.response.reportes;

import com.jairo.workflowtramites.model.enums.Rol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioReporteResponse {

    private String id;
    private String nombre;
    private String email;
    private Rol rol;
    private String departamentoId;
    private String departamentoNombre;
    private boolean activo;
}
