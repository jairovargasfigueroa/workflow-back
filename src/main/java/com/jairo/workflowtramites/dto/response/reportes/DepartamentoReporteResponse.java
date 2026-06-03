package com.jairo.workflowtramites.dto.response.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartamentoReporteResponse {

    private String id;
    private String nombre;
    private boolean activo;
    private long totalFuncionarios;
    private long totalSolicitudesActivas;
}
