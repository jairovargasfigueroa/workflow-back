package com.jairo.workflowtramites.dto.response.motor;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RiesgoFlujoResponse {
    private String flujoId;
    private List<CuelloPredichoResponse> cuellosPredichos;
    private List<DemoraPredichaResponse> demorasPredichas;
    private Double cumplimientoSlaEsperado;
    private boolean disponible;

    public static RiesgoFlujoResponse noDisponible(String flujoId) {
        return RiesgoFlujoResponse.builder()
                .flujoId(flujoId)
                .cuellosPredichos(List.of())
                .demorasPredichas(List.of())
                .disponible(false)
                .build();
    }
}
