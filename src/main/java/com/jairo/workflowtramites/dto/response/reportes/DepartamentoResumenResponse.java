package com.jairo.workflowtramites.dto.response.reportes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resumen minimo de un departamento para reportes: id (drill-down) + nombre (legible).
 * Reemplaza la lista de IDs crudos que se exponia antes en departamentosActuales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartamentoResumenResponse {
    private String id;
    private String nombre;
}
