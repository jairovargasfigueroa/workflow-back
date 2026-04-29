package com.jairo.workflowtramites.model.embeds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaDepartamento {

    private String departamentoId;
    private String departamentoNombre;
    private String elementId;
    private String formularioId;
    private String funcionarioId;
    private String funcionarioNombre;
    private String funcionarioAsignadoId;
    private String funcionarioAsignadoNombre;
    private LocalDateTime fechaAsignacion;
    private String accion;
    private String comentario;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaRespuesta;
    private List<RespuestaCampo> respuestas;
}
