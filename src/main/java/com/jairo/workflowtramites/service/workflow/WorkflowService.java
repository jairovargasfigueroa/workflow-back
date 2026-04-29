package com.jairo.workflowtramites.service.workflow;

import java.util.Map;

public interface WorkflowService {

    WorkflowResult iniciarProceso(String procesoKey, Map<String, Object> variables);

    WorkflowResult completarTarea(String processInstanceId, String departamentoId, Map<String, Object> variables);

    void cancelarProceso(String processInstanceId, String motivo);

    String desplegarProceso(String procesoKey, String nombre, String xml);
}
