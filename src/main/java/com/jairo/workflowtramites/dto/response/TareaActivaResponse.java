package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TareaActivaResponse {

    private String elementId;
    private String departamentoId;
    private String departamentoNombre;
    private List<CampoFormulario> campos;
    private List<AccionDisponibleResponse> acciones;
}
