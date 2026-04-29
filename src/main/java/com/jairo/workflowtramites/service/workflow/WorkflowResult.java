package com.jairo.workflowtramites.service.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResult {
    private String processInstanceId;
    private boolean procesoTerminado;
    private List<TareaActiva> tareasActuales;
    private String estadoFinal;
}
