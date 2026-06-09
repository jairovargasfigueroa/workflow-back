package com.jairo.workflowtramites.dto.response.motor;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MejorRutaResponse {
    private String flujoId;
    private List<RutaPredichaResponse> rutas;
    private String rutaRecomendadaNombre;
    private boolean disponible;

    public static MejorRutaResponse noDisponible(String flujoId) {
        return MejorRutaResponse.builder()
                .flujoId(flujoId)
                .rutas(List.of())
                .disponible(false)
                .build();
    }
}
