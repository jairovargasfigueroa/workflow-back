package com.jairo.workflowtramites.dto.response.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopNItemResponse {

    private String id;
    private String nombre;
    private double valor;
    private String unidad;
}
