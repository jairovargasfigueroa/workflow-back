package com.jairo.workflowtramites.dto.response.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TramiteReporteResponse {

    private String id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private long totalSolicitudes;
}
