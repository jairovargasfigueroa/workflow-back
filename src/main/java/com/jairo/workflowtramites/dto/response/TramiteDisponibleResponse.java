package com.jairo.workflowtramites.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TramiteDisponibleResponse {

    private String id;
    private String nombre;
    private String descripcion;
    private List<String> requisitos;
    private List<String> etiquetas;
    private String formularioSolicitanteId;
}
