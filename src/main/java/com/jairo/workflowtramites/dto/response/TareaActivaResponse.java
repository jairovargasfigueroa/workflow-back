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
    private String carrilId;
    private String carrilNombre;
    private List<CampoFormulario> campos;
    private List<AccionDisponibleResponse> acciones;

    // Documentos que el funcionario debe generar en este nodo (slots de subida).
    // El front los renderiza separados del formulario; al subir, manda
    // campoFormulario = nombre del documento producido.
    private List<DocumentoProducidoSlotResponse> documentosProducidos;
}
