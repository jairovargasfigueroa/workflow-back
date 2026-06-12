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
    private String resumen;        // texto legible que arma el motor (para que el front lo muestre directo)
    private boolean disponible;

    public static MejorRutaResponse noDisponible(String flujoId) {
        return MejorRutaResponse.builder()
                .flujoId(flujoId)
                .rutas(List.of())
                .disponible(false)
                .build();
    }
}
