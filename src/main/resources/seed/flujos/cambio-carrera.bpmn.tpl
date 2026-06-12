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
        <bpmn:flowNodeRef>task_academica</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_DireccionCarrera" name="Direccion de Carrera">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Direccion de Carrera}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_carrera_actual</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_salida</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_fork</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>task_carrera_nueva</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_join</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_Tesoreria" name="Tesoreria">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Tesoreria}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_pago</bpmn:flowNodeRef>
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
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Record academico","formatosAceptados":["pdf"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}},{"nombre":"Carta de motivacion","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:outgoing>sf_start_academica</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_academica" name="Revisi&#243;n acad&#233;mica" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Acta de revision academica","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="4" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_start_academica</bpmn:incoming>
      <bpmn:outgoing>sf_academica_actual</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_carrera_actual" name="Aprobaci&#243;n carrera actual" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Dictamen de admision","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="20" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_academica_actual</bpmn:incoming>
      <bpmn:outgoing>sf_actual_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_salida" name="&#191;Permite salida?">
      <bpmn:incoming>sf_actual_gw</bpmn:incoming>
      <bpmn:outgoing>sf_gw_fork</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_rechazado_inicial</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:parallelGateway id="gw_fork" name="Gestiones en paralelo">
      <bpmn:incoming>sf_gw_fork</bpmn:incoming>
      <bpmn:outgoing>sf_fork_nueva</bpmn:outgoing>
      <bpmn:outgoing>sf_fork_pago</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_carrera_nueva" name="Aprobaci&#243;n nueva carrera" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Resolucion de cambio de carrera","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":true,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="20" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_fork_nueva</bpmn:incoming>
      <bpmn:outgoing>sf_nueva_join</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_pago" name="Validaci&#243;n de pagos" camunda:candidateGroups="{{DEPTO:Tesoreria}}" camunda:formKey="{{FORM:Validacion de pagos}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Comprobante de validacion de pago","formatosAceptados":["pdf","jpg"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Tesoreria}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Tesoreria}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Tesoreria}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="8" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_fork_pago</bpmn:incoming>
      <bpmn:outgoing>sf_pago_join</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:parallelGateway id="gw_join">
      <bpmn:incoming>sf_nueva_join</bpmn:incoming>
      <bpmn:incoming>sf_pago_join</bpmn:incoming>
      <bpmn:outgoing>sf_join_decano</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Dictamen del decano","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="24" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_join_decano</bpmn:incoming>
      <bpmn:outgoing>sf_decano_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_final" name="&#191;Decisi&#243;n final?">
      <bpmn:incoming>sf_decano_gw</bpmn:incoming>
      <bpmn:outgoing>sf_gw_aprobado</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_rechazado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:endEvent id="end_aprobado" name="Aprobado">
      <bpmn:incoming>sf_gw_aprobado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:endEvent id="end_rechazado" name="Rechazado">
      <bpmn:incoming>sf_gw_rechazado</bpmn:incoming>
      <bpmn:incoming>sf_gw_rechazado_inicial</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="sf_start_academica" sourceRef="start" targetRef="task_academica"/>
    <bpmn:sequenceFlow id="sf_academica_actual" sourceRef="task_academica" targetRef="task_carrera_actual"/>
    <bpmn:sequenceFlow id="sf_actual_gw" sourceRef="task_carrera_actual" targetRef="gw_salida"/>
    <bpmn:sequenceFlow id="sf_gw_fork" name="Aprobado" sourceRef="gw_salida" targetRef="gw_fork"/>
    <bpmn:sequenceFlow id="sf_gw_rechazado_inicial" name="Rechazado" sourceRef="gw_salida" targetRef="end_rechazado"/>
    <bpmn:sequenceFlow id="sf_fork_nueva" sourceRef="gw_fork" targetRef="task_carrera_nueva"/>
    <bpmn:sequenceFlow id="sf_fork_pago" sourceRef="gw_fork" targetRef="task_pago"/>
    <bpmn:sequenceFlow id="sf_nueva_join" sourceRef="task_carrera_nueva" targetRef="gw_join"/>
    <bpmn:sequenceFlow id="sf_pago_join" sourceRef="task_pago" targetRef="gw_join"/>
    <bpmn:sequenceFlow id="sf_join_decano" sourceRef="gw_join" targetRef="task_decano"/>
    <bpmn:sequenceFlow id="sf_decano_gw" sourceRef="task_decano" targetRef="gw_final"/>
    <bpmn:sequenceFlow id="sf_gw_aprobado" name="Aprobado" sourceRef="gw_final" targetRef="end_aprobado"/>
    <bpmn:sequenceFlow id="sf_gw_rechazado" name="Rechazado" sourceRef="gw_final" targetRef="end_rechazado"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process">
      <bpmndi:BPMNShape id="Lane_Secretaria_di" bpmnElement="Lane_Secretaria" isHorizontal="true">
        <dc:Bounds x="160" y="80" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_DireccionCarrera_di" bpmnElement="Lane_DireccionCarrera" isHorizontal="true">
        <dc:Bounds x="160" y="240" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Tesoreria_di" bpmnElement="Lane_Tesoreria" isHorizontal="true">
        <dc:Bounds x="160" y="400" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Decanato_di" bpmnElement="Lane_Decanato" isHorizontal="true">
        <dc:Bounds x="160" y="560" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="220" y="142" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_academica_di" bpmnElement="task_academica">
        <dc:Bounds x="400" y="120" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_carrera_actual_di" bpmnElement="task_carrera_actual">
        <dc:Bounds x="580" y="280" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_salida_di" bpmnElement="gw_salida" isMarkerVisible="true">
        <dc:Bounds x="760" y="295" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_fork_di" bpmnElement="gw_fork">
        <dc:Bounds x="940" y="295" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_carrera_nueva_di" bpmnElement="task_carrera_nueva">
        <dc:Bounds x="1120" y="280" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_pago_di" bpmnElement="task_pago">
        <dc:Bounds x="1120" y="440" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_join_di" bpmnElement="gw_join">
        <dc:Bounds x="1300" y="295" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="1480" y="600" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="1660" y="615" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1840" y="590" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="1840" y="655" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_academica_di" bpmnElement="sf_start_academica">
        <di:waypoint x="256" y="160"/>
        <di:waypoint x="400" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_academica_actual_di" bpmnElement="sf_academica_actual">
        <di:waypoint x="500" y="160"/>
        <di:waypoint x="540" y="160"/>
        <di:waypoint x="540" y="320"/>
        <di:waypoint x="580" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_actual_gw_di" bpmnElement="sf_actual_gw">
        <di:waypoint x="680" y="320"/>
        <di:waypoint x="760" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_fork_di" bpmnElement="sf_gw_fork">
        <di:waypoint x="810" y="320"/>
        <di:waypoint x="940" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_inicial_di" bpmnElement="sf_gw_rechazado_inicial">
        <di:waypoint x="785" y="345"/>
        <di:waypoint x="785" y="673"/>
        <di:waypoint x="1840" y="673"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_nueva_di" bpmnElement="sf_fork_nueva">
        <di:waypoint x="990" y="320"/>
        <di:waypoint x="1120" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_pago_di" bpmnElement="sf_fork_pago">
        <di:waypoint x="965" y="345"/>
        <di:waypoint x="965" y="480"/>
        <di:waypoint x="1120" y="480"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_nueva_join_di" bpmnElement="sf_nueva_join">
        <di:waypoint x="1220" y="320"/>
        <di:waypoint x="1300" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_pago_join_di" bpmnElement="sf_pago_join">
        <di:waypoint x="1220" y="480"/>
        <di:waypoint x="1325" y="480"/>
        <di:waypoint x="1325" y="345"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_join_decano_di" bpmnElement="sf_join_decano">
        <di:waypoint x="1325" y="345"/>
        <di:waypoint x="1325" y="640"/>
        <di:waypoint x="1480" y="640"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="1580" y="640"/>
        <di:waypoint x="1660" y="640"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="1685" y="615"/>
        <di:waypoint x="1685" y="608"/>
        <di:waypoint x="1840" y="608"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_di" bpmnElement="sf_gw_rechazado">
        <di:waypoint x="1685" y="665"/>
        <di:waypoint x="1685" y="673"/>
        <di:waypoint x="1840" y="673"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
