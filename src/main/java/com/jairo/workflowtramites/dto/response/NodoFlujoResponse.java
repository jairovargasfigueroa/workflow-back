package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import com.jairo.workflowtramites.model.embeds.ConfiguracionDocumental;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NodoFlujoResponse {

    private String elementId;
    private String tipo;
    private String nombre;
    private String departamentoId;
    private String carrilId;
    private String carrilNombre;
    private String formularioId;
    private List<CampoFormulario> camposFormulario;
    private List<TransicionResponse> transiciones;
    private ConfiguracionDocumental configuracionDocumental;
}
