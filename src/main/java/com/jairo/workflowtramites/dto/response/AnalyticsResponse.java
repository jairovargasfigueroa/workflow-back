package com.jairo.workflowtramites.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsResponse {

    private String tramiteId;
    private String nombreTramite;
    private List<CuelloDeBottellaResponse> cuellosDeBottella;
    private List<RecomendacionResponse> recomendaciones;
}
