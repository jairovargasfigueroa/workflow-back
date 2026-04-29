package com.jairo.workflowtramites.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DepartamentoResponse {

    private String id;
    private String nombre;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}
