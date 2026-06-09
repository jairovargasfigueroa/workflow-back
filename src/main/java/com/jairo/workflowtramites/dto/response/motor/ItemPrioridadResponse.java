package com.jairo.workflowtramites.dto.response.motor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemPrioridadResponse {
    private String solicitudId;
    private String tramiteNombre;
    private String prioridad;
    private String razon;
    private Double score;
    private String estadoSla;          // bonus del micro (VERDE/AMARILLO/ROJO/VENCIDO)
    private Double horasHastaLimite;   // bonus del micro
}
