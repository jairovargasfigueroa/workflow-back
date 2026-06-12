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
        <bpmn:flowNodeRef>task_correccion</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_Direccion" name="Direccion de Carrera">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Direccion de Carrera}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_direccion</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_decision</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>end_rechazado</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_Tesoreria" name="Tesoreria">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Tesoreria}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_pago</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>end_aprobado</bpmn:flowNodeRef>
      </bpmn:lane>
    </bpmn:laneSet>
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Record academico de universidad de origen","formatosAceptados":["pdf"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}},{"nombre":"Syllabus de materias","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:outgoing>sf_start_revision</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_revision" name="Revisi&#243;n de documentos" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Acta de revision academica","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="4" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_start_revision</bpmn:incoming>
      <bpmn:outgoing>sf_revision_direccion</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_direccion" name="Aprobaci&#243;n de direcci&#243;n" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Resolucion de convalidacion","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":true,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="20" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_revision_direccion</bpmn:incoming>
      <bpmn:incoming>sf_correccion_direccion</bpmn:incoming>
      <bpmn:outgoing>sf_direccion_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_decision" name="&#191;Decisi&#243;n?">
      <bpmn:incoming>sf_direccion_gw</bpmn:incoming>
      <bpmn:outgoing>sf_gw_aprobado</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_observado</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_rechazado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:userTask id="task_correccion" name="Correcci&#243;n de solicitud" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Observaciones academicas}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Informe de observaciones academicas","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="4" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_gw_observado</bpmn:incoming>
      <bpmn:outgoing>sf_correccion_direccion</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_pago" name="Validaci&#243;n de pagos" camunda:candidateGroups="{{DEPTO:Tesoreria}}" camunda:formKey="{{FORM:Validacion de pagos}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Comprobante de validacion de pago","formatosAceptados":["pdf","jpg"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Tesoreria}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Tesoreria}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Tesoreria}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="8" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_gw_aprobado</bpmn:incoming>
      <bpmn:outgoing>sf_pago_end</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:endEvent id="end_aprobado" name="Aprobado">
      <bpmn:incoming>sf_pago_end</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:endEvent id="end_rechazado" name="Rechazado">
      <bpmn:incoming>sf_gw_rechazado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="sf_start_revision" sourceRef="start" targetRef="task_revision"/>
    <bpmn:sequenceFlow id="sf_revision_direccion" sourceRef="task_revision" targetRef="task_direccion"/>
    <bpmn:sequenceFlow id="sf_direccion_gw" sourceRef="task_direccion" targetRef="gw_decision"/>
    <bpmn:sequenceFlow id="sf_gw_aprobado" name="Aprobado" sourceRef="gw_decision" targetRef="task_pago"/>
    <bpmn:sequenceFlow id="sf_gw_observado" name="Observado" sourceRef="gw_decision" targetRef="task_correccion"/>
    <bpmn:sequenceFlow id="sf_gw_rechazado" name="Rechazado" sourceRef="gw_decision" targetRef="end_rechazado"/>
    <bpmn:sequenceFlow id="sf_correccion_direccion" name="Corregido" sourceRef="task_correccion" targetRef="task_direccion"/>
    <bpmn:sequenceFlow id="sf_pago_end" sourceRef="task_pago" targetRef="end_aprobado"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process">
      <bpmndi:BPMNShape id="Lane_Secretaria_di" bpmnElement="Lane_Secretaria" isHorizontal="true">
        <dc:Bounds x="160" y="80" width="1220" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Direccion_di" bpmnElement="Lane_Direccion" isHorizontal="true">
        <dc:Bounds x="160" y="240" width="1220" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Tesoreria_di" bpmnElement="Lane_Tesoreria" isHorizontal="true">
        <dc:Bounds x="160" y="400" width="1220" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="220" y="142" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_revision_di" bpmnElement="task_revision">
        <dc:Bounds x="400" y="120" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_direccion_di" bpmnElement="task_direccion">
        <dc:Bounds x="580" y="280" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_decision_di" bpmnElement="gw_decision" isMarkerVisible="true">
        <dc:Bounds x="760" y="295" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_correccion_di" bpmnElement="task_correccion">
        <dc:Bounds x="940" y="120" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="942" y="302" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_pago_di" bpmnElement="task_pago">
        <dc:Bounds x="940" y="440" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1122" y="462" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_revision_di" bpmnElement="sf_start_revision">
        <di:waypoint x="256" y="160"/>
        <di:waypoint x="400" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_revision_direccion_di" bpmnElement="sf_revision_direccion">
        <di:waypoint x="500" y="160"/>
        <di:waypoint x="540" y="160"/>
        <di:waypoint x="540" y="320"/>
        <di:waypoint x="580" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_direccion_gw_di" bpmnElement="sf_direccion_gw">
        <di:waypoint x="680" y="320"/>
        <di:waypoint x="760" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="785" y="345"/>
        <di:waypoint x="785" y="480"/>
        <di:waypoint x="940" y="480"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_observado_di" bpmnElement="sf_gw_observado">
        <di:waypoint x="785" y="295"/>
        <di:waypoint x="785" y="160"/>
        <di:waypoint x="940" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_di" bpmnElement="sf_gw_rechazado">
        <di:waypoint x="810" y="320"/>
        <di:waypoint x="942" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_correccion_direccion_di" bpmnElement="sf_correccion_direccion">
        <di:waypoint x="990" y="200"/>
        <di:waypoint x="990" y="240"/>
        <di:waypoint x="630" y="240"/>
        <di:waypoint x="630" y="280"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_pago_end_di" bpmnElement="sf_pago_end">
        <di:waypoint x="1040" y="480"/>
        <di:waypoint x="1122" y="480"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
