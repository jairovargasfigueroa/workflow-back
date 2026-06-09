package com.jairo.workflowtramites.dto.response.motor;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardPrioridadResponse {
    private int totalConPrioridadAlta;
    private int totalConPrioridadMedia;
    private int totalConPrioridadBaja;
    private List<ItemPrioridadResponse> topUrgentes;
    private boolean disponible;

    public static DashboardPrioridadResponse noDisponible() {
        return DashboardPrioridadResponse.builder()
                .totalConPrioridadAlta(0)
                .totalConPrioridadMedia(0)
                .totalConPrioridadBaja(0)
                .topUrgentes(List.of())
                .disponible(false)
                .build();
    }
}
