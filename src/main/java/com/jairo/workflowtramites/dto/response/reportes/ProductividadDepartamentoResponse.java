package com.jairo.workflowtramites.dto.response.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductividadDepartamentoResponse {

    private String departamentoId;
    private String departamentoNombre;
    private long totalSolicitudesProcesadas;
    private double tiempoPromedioHoras;
    private double tasaRechazo;
    private long funcionariosActivos;
}
