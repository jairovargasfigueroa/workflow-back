package com.jairo.workflowtramites.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Una accion del catalogo, para que el front arme el dropdown del editor de flujos. */
@Data
@AllArgsConstructor
public class AccionCatalogoResponse {
    private String etiqueta;   // lo que se muestra (ej: "Aprobado")
    private String valor;      // lo que viaja como accion (ej: "aprobado")
    private boolean esFinal;   // si puede ser el nombre de un evento de fin
}
