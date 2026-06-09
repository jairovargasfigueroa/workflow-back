package com.jairo.workflowtramites.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Slot de un documento que el funcionario debe generar (producir) en el nodo.
 * Se identifica por {@code nombre} — ese es el valor que el front debe enviar
 * como {@code campoFormulario} al subir el archivo, para que el back aplique
 * los permisos del documento producido correspondiente.
 */
@Data
@Builder
public class DocumentoProducidoSlotResponse {
    private String nombre;
    private List<String> formatosAceptados;
    private boolean obligatorio;
    private boolean inmutablePostCierre;
}
