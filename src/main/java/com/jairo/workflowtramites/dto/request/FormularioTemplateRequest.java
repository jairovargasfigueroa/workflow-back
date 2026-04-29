package com.jairo.workflowtramites.dto.request;

import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormularioTemplateRequest {

    @NotBlank
    private String titulo;

    private String descripcion;

    @NotNull
    private List<CampoFormulario> campos;
}
