package com.jairo.workflowtramites.dto.request;

import com.jairo.workflowtramites.model.embeds.RespuestaCampo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaDepartamentoRequest {

    @NotBlank
    private String departamentoId;

    @NotBlank
    private String elementId;

    @NotBlank
    private String accion;

    private String comentario;

    private List<RespuestaCampo> respuestas;
}
