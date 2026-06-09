package com.jairo.workflowtramites.dto.response.motor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CuelloPredichoResponse {
    private String elementId;
    private String nodoNombre;
    private String departamentoId;
    private Double probabilidad;
    private String cuando;
}
