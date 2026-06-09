package com.jairo.workflowtramites.dto.response.motor;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RutaPredichaResponse {
    private String nombre;
    private List<String> nodos;
    private Double tiempoPredichoHoras;
    private Double cumplimientoSlaPorcentaje;
    private boolean recomendada;
}
