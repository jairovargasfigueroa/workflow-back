<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="process" isExecutable="true">
    <bpmn:laneSet id="LaneSet_1">
      <bpmn:lane id="Lane_Secretaria" name="Secretaria Academica">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Secretaria Academica}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>start</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>task_revision</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_Decanato" name="Decanato">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Decanato}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_decano</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_final</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>end_aprobado</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>end_rechazado</bpmn:flowNodeRef>
      </bpmn:lane>
    </bpmn:laneSet>
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Certificado medico","formatosAceptados":["pdf","jpg"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}},{"nombre":"Carta de justificacion","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:outgoing>sf_start_revision</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_revision" name="Revisi&#243;n de justificativo" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Acta de revision academica","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="4" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_start_revision</bpmn:incoming>
      <bpmn:outgoing>sf_revision_decano</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Resolucion de justificacion","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":true,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="24" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_revision_decano</bpmn:incoming>
      <bpmn:outgoing>sf_decano_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_final" name="&#191;Justifica la falta?">
      <bpmn:incoming>sf_decano_gw</bpmn:incoming>
      <bpmn:outgoing>sf_gw_aprobado</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_rechazado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:endEvent id="end_aprobado" name="Aprobado">
      <bpmn:incoming>sf_gw_aprobado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:endEvent id="end_rechazado" name="Rechazado">
      <bpmn:incoming>sf_gw_rechazado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="sf_start_revision" sourceRef="start" targetRef="task_revision"/>
    <bpmn:sequenceFlow id="sf_revision_decano" sourceRef="task_revision" targetRef="task_decano"/>
    <bpmn:sequenceFlow id="sf_decano_gw" sourceRef="task_decano" targetRef="gw_final"/>
    <bpmn:sequenceFlow id="sf_gw_aprobado" name="Aprobado" sourceRef="gw_final" targetRef="end_aprobado"/>
    <bpmn:sequenceFlow id="sf_gw_rechazado" name="Rechazado" sourceRef="gw_final" targetRef="end_rechazado"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process">
      <bpmndi:BPMNShape id="Lane_Secretaria_di" bpmnElement="Lane_Secretaria" isHorizontal="true">
        <dc:Bounds x="160" y="80" width="1040" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Decanato_di" bpmnElement="Lane_Decanato" isHorizontal="true">
        <dc:Bounds x="160" y="240" width="1040" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="220" y="142" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_revision_di" bpmnElement="task_revision">
        <dc:Bounds x="400" y="120" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="580" y="280" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="760" y="295" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="940" y="270" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="940" y="335" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_revision_di" bpmnElement="sf_start_revision">
        <di:waypoint x="256" y="160"/>
        <di:waypoint x="400" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_revision_decano_di" bpmnElement="sf_revision_decano">
        <di:waypoint x="500" y="160"/>
        <di:waypoint x="540" y="160"/>
        <di:waypoint x="540" y="320"/>
        <di:waypoint x="580" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="680" y="320"/>
        <di:waypoint x="760" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="810" y="320"/>
        <di:waypoint x="875" y="320"/>
        <di:waypoint x="875" y="288"/>
        <di:waypoint x="940" y="288"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_di" bpmnElement="sf_gw_rechazado">
        <di:waypoint x="810" y="320"/>
        <di:waypoint x="875" y="320"/>
        <di:waypoint x="875" y="353"/>
        <di:waypoint x="940" y="353"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
