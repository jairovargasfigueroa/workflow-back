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
        <bpmn:flowNodeRef>gw_direccion</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>end_rechazado</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_Investigacion" name="Investigacion">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Investigacion}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_comite</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_comite</bpmn:flowNodeRef>
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
      </bpmn:lane>
    </bpmn:laneSet>
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Propuesta de investigacion firmada","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}},{"nombre":"Record academico","formatosAceptados":["pdf"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:outgoing>sf_start_revision</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_revision" name="Revisi&#243;n de propuesta" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
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
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Resolucion de direccion de carrera","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="20" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_revision_direccion</bpmn:incoming>
      <bpmn:incoming>sf_correccion_direccion</bpmn:incoming>
      <bpmn:outgoing>sf_direccion_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_direccion" name="&#191;Avala la direcci&#243;n?">
      <bpmn:incoming>sf_direccion_gw</bpmn:incoming>
      <bpmn:outgoing>sf_gw_comite</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_observado</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_rechazado_inicial</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:userTask id="task_correccion" name="Correcci&#243;n de propuesta" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Observaciones academicas}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Informe de observaciones academicas","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="4" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_gw_observado</bpmn:incoming>
      <bpmn:outgoing>sf_correccion_direccion</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_comite" name="Evaluaci&#243;n del comit&#233;" camunda:candidateGroups="{{DEPTO:Investigacion}}" camunda:formKey="{{FORM:Aprobacion de investigacion}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Acta del comite de investigacion","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":true,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Investigacion}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Investigacion}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="60" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_gw_comite</bpmn:incoming>
      <bpmn:outgoing>sf_comite_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_comite" name="&#191;Comit&#233; aprueba?">
      <bpmn:incoming>sf_comite_gw</bpmn:incoming>
      <bpmn:outgoing>sf_comite_decano</bpmn:outgoing>
      <bpmn:outgoing>sf_comite_rechazado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Dictamen del decano","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="24" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_comite_decano</bpmn:incoming>
      <bpmn:outgoing>sf_decano_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_final" name="&#191;Decisi&#243;n final?">
      <bpmn:incoming>sf_decano_gw</bpmn:incoming>
      <bpmn:outgoing>sf_gw_aprobado</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_rechazado_final</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:endEvent id="end_aprobado" name="Aprobado">
      <bpmn:incoming>sf_gw_aprobado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:endEvent id="end_rechazado" name="Rechazado">
      <bpmn:incoming>sf_gw_rechazado_inicial</bpmn:incoming>
      <bpmn:incoming>sf_comite_rechazado</bpmn:incoming>
      <bpmn:incoming>sf_gw_rechazado_final</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="sf_start_revision" sourceRef="start" targetRef="task_revision"/>
    <bpmn:sequenceFlow id="sf_revision_direccion" sourceRef="task_revision" targetRef="task_direccion"/>
    <bpmn:sequenceFlow id="sf_direccion_gw" sourceRef="task_direccion" targetRef="gw_direccion"/>
    <bpmn:sequenceFlow id="sf_gw_comite" name="Aprobado" sourceRef="gw_direccion" targetRef="task_comite"/>
    <bpmn:sequenceFlow id="sf_gw_observado" name="Observado" sourceRef="gw_direccion" targetRef="task_correccion"/>
    <bpmn:sequenceFlow id="sf_gw_rechazado_inicial" name="Rechazado" sourceRef="gw_direccion" targetRef="end_rechazado"/>
    <bpmn:sequenceFlow id="sf_correccion_direccion" name="Corregido" sourceRef="task_correccion" targetRef="task_direccion"/>
    <bpmn:sequenceFlow id="sf_comite_gw" sourceRef="task_comite" targetRef="gw_comite"/>
    <bpmn:sequenceFlow id="sf_comite_decano" name="Aprobado" sourceRef="gw_comite" targetRef="task_decano"/>
    <bpmn:sequenceFlow id="sf_comite_rechazado" name="Rechazado" sourceRef="gw_comite" targetRef="end_rechazado"/>
    <bpmn:sequenceFlow id="sf_decano_gw" sourceRef="task_decano" targetRef="gw_final"/>
    <bpmn:sequenceFlow id="sf_gw_aprobado" name="Aprobado" sourceRef="gw_final" targetRef="end_aprobado"/>
    <bpmn:sequenceFlow id="sf_gw_rechazado_final" name="Rechazado" sourceRef="gw_final" targetRef="end_rechazado"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process">
      <bpmndi:BPMNShape id="Lane_Secretaria_di" bpmnElement="Lane_Secretaria" isHorizontal="true">
        <dc:Bounds x="160" y="80" width="1760" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Direccion_di" bpmnElement="Lane_Direccion" isHorizontal="true">
        <dc:Bounds x="160" y="240" width="1760" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Investigacion_di" bpmnElement="Lane_Investigacion" isHorizontal="true">
        <dc:Bounds x="160" y="400" width="1760" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Decanato_di" bpmnElement="Lane_Decanato" isHorizontal="true">
        <dc:Bounds x="160" y="560" width="1760" height="160"/>
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
      <bpmndi:BPMNShape id="gw_direccion_di" bpmnElement="gw_direccion" isMarkerVisible="true">
        <dc:Bounds x="760" y="295" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_correccion_di" bpmnElement="task_correccion">
        <dc:Bounds x="940" y="120" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="940" y="302" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_comite_di" bpmnElement="task_comite">
        <dc:Bounds x="940" y="440" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_comite_di" bpmnElement="gw_comite" isMarkerVisible="true">
        <dc:Bounds x="1120" y="455" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="1300" y="600" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="1480" y="615" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1660" y="622" width="36" height="36"/>
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
      <bpmndi:BPMNEdge id="sf_gw_comite_di" bpmnElement="sf_gw_comite">
        <di:waypoint x="810" y="320"/>
        <di:waypoint x="875" y="320"/>
        <di:waypoint x="875" y="480"/>
        <di:waypoint x="940" y="480"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_observado_di" bpmnElement="sf_gw_observado">
        <di:waypoint x="785" y="295"/>
        <di:waypoint x="785" y="230"/>
        <di:waypoint x="990" y="230"/>
        <di:waypoint x="990" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_inicial_di" bpmnElement="sf_gw_rechazado_inicial">
        <di:waypoint x="810" y="320"/>
        <di:waypoint x="940" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_correccion_direccion_di" bpmnElement="sf_correccion_direccion">
        <di:waypoint x="940" y="160"/>
        <di:waypoint x="630" y="160"/>
        <di:waypoint x="630" y="280"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_comite_gw_di" bpmnElement="sf_comite_gw">
        <di:waypoint x="1040" y="480"/>
        <di:waypoint x="1120" y="480"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_comite_decano_di" bpmnElement="sf_comite_decano">
        <di:waypoint x="1170" y="480"/>
        <di:waypoint x="1235" y="480"/>
        <di:waypoint x="1235" y="640"/>
        <di:waypoint x="1300" y="640"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_comite_rechazado_di" bpmnElement="sf_comite_rechazado">
        <di:waypoint x="1145" y="455"/>
        <di:waypoint x="1145" y="320"/>
        <di:waypoint x="976" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="1400" y="640"/>
        <di:waypoint x="1480" y="640"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="1530" y="640"/>
        <di:waypoint x="1660" y="640"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_final_di" bpmnElement="sf_gw_rechazado_final">
        <di:waypoint x="1505" y="615"/>
        <di:waypoint x="1505" y="370"/>
        <di:waypoint x="958" y="370"/>
        <di:waypoint x="958" y="338"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
