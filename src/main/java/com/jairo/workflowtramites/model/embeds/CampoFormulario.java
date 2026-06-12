package com.jairo.workflowtramites.model.embeds;

import com.jairo.workflowtramites.model.enums.TipoCampo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampoFormulario {

    private String nombre;
    private String etiqueta;
    private TipoCampo tipo;
    private boolean requerido;
    private List<String> opciones;   // SELECT / RADIO / CHECKBOX: los valores a elegir
    private List<String> columnas;   // TABLA: nombres de columnas · GRID: opciones por fila
    private List<String> filas;      // GRID: las filas fijas (preguntas)
}
