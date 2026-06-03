package com.jairo.workflowtramites.dto.response.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricaTiempoResponse {

    private String id;
    private String nombre;
    private double tiempoPromedioHoras;
    private double tiempoMedianaHoras;
    private double tiempoMaxHoras;
    private long totalSolicitudes;
}
