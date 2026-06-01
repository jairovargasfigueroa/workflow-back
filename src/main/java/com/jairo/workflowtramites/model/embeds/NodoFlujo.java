package com.jairo.workflowtramites.model.embeds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodoFlujo {

    private String elementId;
    private String tipo;
    private String nombre;
    private String departamentoId;
    private String carrilId;
    private String carrilNombre;
    private String formularioId;
    private List<CampoFormulario> camposFormulario;
    private List<TransicionFlujo> transiciones;
    private ConfiguracionDocumental configuracionDocumental;
}
