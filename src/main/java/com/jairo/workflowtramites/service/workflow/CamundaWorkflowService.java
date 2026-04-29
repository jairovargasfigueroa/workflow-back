package com.jairo.workflowtramites.service.workflow;

import com.jairo.workflowtramites.exception.ValidacionBpmnException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ParseException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CamundaWorkflowService implements WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;

    @Override
    public WorkflowResult iniciarProceso(String procesoKey, Map<String, Object> variables) {
        ProcessInstance process = runtimeService.startProcessInstanceByKey(procesoKey, variables);

        return WorkflowResult.builder()
                .processInstanceId(process.getId())
                .procesoTerminado(false)
                .tareasActuales(obtenerTareasActuales(process.getId()))
                .build();
    }

    @Override
    public WorkflowResult completarTarea(String processInstanceId, String departamentoId, Map<String, Object> variables) {
        Task tarea = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskCandidateGroup(departamentoId)
                .singleResult();

        if (tarea == null) {
            throw new RuntimeException("No hay tarea activa para el departamento '"
                    + departamentoId + "' en el proceso: " + processInstanceId);
        }

        taskService.complete(tarea.getId(), variables);

        List<TareaActiva> tareasRestantes = obtenerTareasActuales(processInstanceId);

        if (!tareasRestantes.isEmpty()) {
            return WorkflowResult.builder()
                    .processInstanceId(processInstanceId)
                    .procesoTerminado(false)
                    .tareasActuales(tareasRestantes)
                    .build();
        }

        HistoricActivityInstance endEvent = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("noneEndEvent")
                .finished()
                .singleResult();

        return WorkflowResult.builder()
                .processInstanceId(processInstanceId)
                .procesoTerminado(true)
                .estadoFinal(endEvent != null ? endEvent.getActivityName() : null)
                .build();
    }

    @Override
    public void cancelarProceso(String processInstanceId, String motivo) {
        runtimeService.deleteProcessInstance(processInstanceId, motivo);
    }

    @Override
    public String desplegarProceso(String procesoKey, String nombre, String xml) {
        String xmlCorregido = xml.replaceFirst(
                "(<bpmn:process\\s+id=\")[^\"]*\"",
                "$1" + procesoKey + "\"");
        xmlCorregido = xmlCorregido.replaceFirst(
                "(bpmnElement=\")[^\"]*\"",
                "$1" + procesoKey + "\"");

        try {
            return repositoryService.createDeployment()
                    .name(nombre)
                    .addString(procesoKey + ".bpmn", xmlCorregido)
                    .deploy()
                    .getId();
        } catch (ParseException e) {
            List<String> errores = new ArrayList<>();
            e.getResorceReports().forEach(r ->
                    r.getErrors().forEach(err -> errores.add(err.getMessage())));
            if (errores.isEmpty()) errores.add(e.getMessage());
            throw new ValidacionBpmnException(errores);
        }
    }

    private List<TareaActiva> obtenerTareasActuales(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .flatMap(tarea -> taskService.getIdentityLinksForTask(tarea.getId())
                        .stream()
                        .filter(link -> "candidate".equals(link.getType()))
                        .filter(link -> link.getGroupId() != null)
                        .map(link -> TareaActiva.builder()
                                .departamentoId(link.getGroupId())
                                .elementId(tarea.getTaskDefinitionKey())
                                .build()))
                .toList();
    }
}
