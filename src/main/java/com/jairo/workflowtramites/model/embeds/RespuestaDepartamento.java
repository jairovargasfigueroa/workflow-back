package com.jairo.workflowtramites.model.embeds;

import com.jairo.workflowtramites.model.enums.EstadoSla;
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
    private String accionEtiqueta;   // etiqueta legible de la accion para el historial ("Continuar", "Aprobado")
    private String comentario;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaRespuesta;
    private List<RespuestaCampo> respuestas;

    // SLA del nodo a nivel de instancia (calculado al entrar al nodo, actualizado por SlaMonitorJob)
    private LocalDateTime fechaLimiteNodo;
    private EstadoSla estadoSlaNodo;
}
