package com.jairo.workflowtramites.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CuelloDeBottellaResponse {

    private String departamentoId;
    private String nombre;
    private double promedioHoras;
    private double importanciaModelo;
}
