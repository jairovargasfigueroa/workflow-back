package com.jairo.workflowtramites.dto.response.motor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnomaliaResponse {
    private String id;
    private String tipo;
    private String severidad;
    private String descripcion;
    private Double score;
    // String (no LocalDateTime) para tolerar cualquier formato de fecha que mande el micro
    // sin romper la deserializacion. El front la formatea.
    private String detectada;
    private String contextoSla;
    private String solicitudIdAfectada;
    private String usuarioIdAfectado;
}
