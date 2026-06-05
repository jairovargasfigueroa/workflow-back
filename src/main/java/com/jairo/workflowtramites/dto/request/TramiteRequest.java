package com.jairo.workflowtramites.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramiteRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    private String formularioSolicitanteId;

    private String flujoTrabajoId;

    private List<String> requisitos;

    private List<String> etiquetas;
}
