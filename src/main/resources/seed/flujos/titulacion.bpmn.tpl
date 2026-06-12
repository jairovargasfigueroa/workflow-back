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
        <bpmn:flowNodeRef>gw_docs</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>task_correccion</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_subsanado</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>end_cancelado</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_Direccion" name="Direccion de Carrera">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Direccion de Carrera}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_direccion</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_fork</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_Biblioteca" name="Biblioteca">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Biblioteca}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_biblioteca</bpmn:flowNodeRef>
      </bpmn:lane>
      <bpmn:lane id="Lane_Tesoreria" name="Tesoreria">
        <bpmn:extensionElements>
          <camunda:properties>
            <camunda:property name="departamentoId" value="{{DEPTO:Tesoreria}}" />
          </camunda:properties>
        </bpmn:extensionElements>
        <bpmn:flowNodeRef>task_pago</bpmn:flowNodeRef>
        <bpmn:flowNodeRef>gw_join</bpmn:flowNodeRef>
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
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Certificado de practicas preprofesionales","formatosAceptados":["pdf"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}},{"nombre":"Record academico","formatosAceptados":["pdf"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DUENO_TRAMITE"}],"editores":[],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DUENO_TRAMITE"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
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
      <bpmn:incoming>sf_subsanado_revision</bpmn:incoming>
      <bpmn:outgoing>sf_revision_gw_docs</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_docs" name="&#191;Documentos completos?">
      <bpmn:incoming>sf_revision_gw_docs</bpmn:incoming>
      <bpmn:outgoing>sf_docs_direccion</bpmn:outgoing>
      <bpmn:outgoing>sf_docs_correccion</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:userTask id="task_correccion" name="Correcci&#243;n de documentos" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Observaciones academicas}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Informe de observaciones academicas","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Secretaria Academica}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="4" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_docs_correccion</bpmn:incoming>
      <bpmn:outgoing>sf_correccion_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_subsanado" name="&#191;Subsanado?">
      <bpmn:incoming>sf_correccion_gw</bpmn:incoming>
      <bpmn:outgoing>sf_subsanado_revision</bpmn:outgoing>
      <bpmn:outgoing>sf_cancelado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:userTask id="task_direccion" name="Aprobaci&#243;n de propuesta" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Resolucion de direccion de carrera","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Direccion de Carrera}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="20" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_docs_direccion</bpmn:incoming>
      <bpmn:outgoing>sf_direccion_fork</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:parallelGateway id="gw_fork" name="Verificaci&#243;n en paralelo">
      <bpmn:incoming>sf_direccion_fork</bpmn:incoming>
      <bpmn:outgoing>sf_fork_biblioteca</bpmn:outgoing>
      <bpmn:outgoing>sf_fork_pago</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_biblioteca" name="Verificaci&#243;n de biblioteca" camunda:candidateGroups="{{DEPTO:Biblioteca}}" camunda:formKey="{{FORM:Verificacion de biblioteca}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Certificado de no adeudo a biblioteca","formatosAceptados":["pdf"],"obligatorio":false,"inmutablePostCierre":false,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Biblioteca}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Biblioteca}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Biblioteca}}"}]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="2" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_fork_biblioteca</bpmn:incoming>
      <bpmn:outgoing>sf_biblioteca_join</bpmn:outgoing>
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
      <bpmn:incoming>sf_biblioteca_join</bpmn:incoming>
      <bpmn:incoming>sf_pago_join</bpmn:incoming>
      <bpmn:outgoing>sf_join_decano</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
      <bpmn:extensionElements>
        <camunda:properties>
          <camunda:property name="configuracionDocumental" value='{"documentosProducidos":[{"nombre":"Acta de grado","formatosAceptados":["pdf","docx"],"obligatorio":false,"inmutablePostCierre":true,"permisos":{"subidores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}],"editores":[{"tipo":"DEPARTAMENTO","sujetoId":"{{DEPTO:Decanato}}"}],"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"eliminadores":[]}}],"permisosDefaultAdHoc":{"lectores":[{"tipo":"TODOS_SIGUIENTES_NODOS"}],"editores":[],"eliminadores":[]}}' />
          <camunda:property name="slaNodoHoras" value="24" />
        </camunda:properties>
      </bpmn:extensionElements>
      <bpmn:incoming>sf_join_decano</bpmn:incoming>
      <bpmn:outgoing>sf_decano_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_final" name="&#191;Decisi&#243;n final?">
      <bpmn:incoming>sf_decano_gw</bpmn:incoming>
      <bpmn:outgoing>sf_final_aprobado</bpmn:outgoing>
      <bpmn:outgoing>sf_final_rechazado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:endEvent id="end_aprobado" name="Aprobado">
      <bpmn:incoming>sf_final_aprobado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:endEvent id="end_rechazado" name="Rechazado">
      <bpmn:incoming>sf_final_rechazado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:endEvent id="end_cancelado" name="Cancelado">
      <bpmn:incoming>sf_cancelado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="sf_start_revision" sourceRef="start" targetRef="task_revision"/>
    <bpmn:sequenceFlow id="sf_revision_gw_docs" sourceRef="task_revision" targetRef="gw_docs"/>
    <bpmn:sequenceFlow id="sf_docs_direccion" name="Aprobado" sourceRef="gw_docs" targetRef="task_direccion"/>
    <bpmn:sequenceFlow id="sf_docs_correccion" name="Rechazado" sourceRef="gw_docs" targetRef="task_correccion"/>
    <bpmn:sequenceFlow id="sf_correccion_gw" sourceRef="task_correccion" targetRef="gw_subsanado"/>
    <bpmn:sequenceFlow id="sf_subsanado_revision" name="Corregido" sourceRef="gw_subsanado" targetRef="task_revision"/>
    <bpmn:sequenceFlow id="sf_cancelado" name="Cancelado" sourceRef="gw_subsanado" targetRef="end_cancelado"/>
    <bpmn:sequenceFlow id="sf_direccion_fork" sourceRef="task_direccion" targetRef="gw_fork"/>
    <bpmn:sequenceFlow id="sf_fork_biblioteca" sourceRef="gw_fork" targetRef="task_biblioteca"/>
    <bpmn:sequenceFlow id="sf_fork_pago" sourceRef="gw_fork" targetRef="task_pago"/>
    <bpmn:sequenceFlow id="sf_biblioteca_join" sourceRef="task_biblioteca" targetRef="gw_join"/>
    <bpmn:sequenceFlow id="sf_pago_join" sourceRef="task_pago" targetRef="gw_join"/>
    <bpmn:sequenceFlow id="sf_join_decano" sourceRef="gw_join" targetRef="task_decano"/>
    <bpmn:sequenceFlow id="sf_decano_gw" sourceRef="task_decano" targetRef="gw_final"/>
    <bpmn:sequenceFlow id="sf_final_aprobado" name="Aprobado" sourceRef="gw_final" targetRef="end_aprobado"/>
    <bpmn:sequenceFlow id="sf_final_rechazado" name="Rechazado" sourceRef="gw_final" targetRef="end_rechazado"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process">
      <bpmndi:BPMNShape id="Lane_Secretaria_di" bpmnElement="Lane_Secretaria" isHorizontal="true">
        <dc:Bounds x="160" y="80" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Direccion_di" bpmnElement="Lane_Direccion" isHorizontal="true">
        <dc:Bounds x="160" y="240" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Biblioteca_di" bpmnElement="Lane_Biblioteca" isHorizontal="true">
        <dc:Bounds x="160" y="400" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Tesoreria_di" bpmnElement="Lane_Tesoreria" isHorizontal="true">
        <dc:Bounds x="160" y="560" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Lane_Decanato_di" bpmnElement="Lane_Decanato" isHorizontal="true">
        <dc:Bounds x="160" y="720" width="1940" height="160"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="220" y="142" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_revision_di" bpmnElement="task_revision">
        <dc:Bounds x="400" y="120" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_docs_di" bpmnElement="gw_docs" isMarkerVisible="true">
        <dc:Bounds x="580" y="135" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_correccion_di" bpmnElement="task_correccion">
        <dc:Bounds x="760" y="120" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_subsanado_di" bpmnElement="gw_subsanado" isMarkerVisible="true">
        <dc:Bounds x="940" y="135" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_cancelado_di" bpmnElement="end_cancelado">
        <dc:Bounds x="1120" y="142" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_direccion_di" bpmnElement="task_direccion">
        <dc:Bounds x="760" y="280" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_fork_di" bpmnElement="gw_fork">
        <dc:Bounds x="940" y="295" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_biblioteca_di" bpmnElement="task_biblioteca">
        <dc:Bounds x="1120" y="440" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_pago_di" bpmnElement="task_pago">
        <dc:Bounds x="1120" y="600" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_join_di" bpmnElement="gw_join">
        <dc:Bounds x="1300" y="615" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="1480" y="760" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="1660" y="775" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1840" y="750" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="1840" y="815" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_revision_di" bpmnElement="sf_start_revision">
        <di:waypoint x="256" y="160"/>
        <di:waypoint x="400" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_revision_gw_docs_di" bpmnElement="sf_revision_gw_docs">
        <di:waypoint x="500" y="160"/>
        <di:waypoint x="580" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_docs_direccion_di" bpmnElement="sf_docs_direccion">
        <di:waypoint x="605" y="185"/>
        <di:waypoint x="605" y="320"/>
        <di:waypoint x="760" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_docs_correccion_di" bpmnElement="sf_docs_correccion">
        <di:waypoint x="630" y="160"/>
        <di:waypoint x="760" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_correccion_gw_di" bpmnElement="sf_correccion_gw">
        <di:waypoint x="860" y="160"/>
        <di:waypoint x="940" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_subsanado_revision_di" bpmnElement="sf_subsanado_revision">
        <di:waypoint x="965" y="185"/>
        <di:waypoint x="965" y="220"/>
        <di:waypoint x="450" y="220"/>
        <di:waypoint x="450" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_cancelado_di" bpmnElement="sf_cancelado">
        <di:waypoint x="990" y="160"/>
        <di:waypoint x="1120" y="160"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_direccion_fork_di" bpmnElement="sf_direccion_fork">
        <di:waypoint x="860" y="320"/>
        <di:waypoint x="940" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_biblioteca_di" bpmnElement="sf_fork_biblioteca">
        <di:waypoint x="965" y="345"/>
        <di:waypoint x="965" y="480"/>
        <di:waypoint x="1120" y="480"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_pago_di" bpmnElement="sf_fork_pago">
        <di:waypoint x="965" y="345"/>
        <di:waypoint x="965" y="640"/>
        <di:waypoint x="1120" y="640"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_biblioteca_join_di" bpmnElement="sf_biblioteca_join">
        <di:waypoint x="1220" y="480"/>
        <di:waypoint x="1325" y="480"/>
        <di:waypoint x="1325" y="615"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_pago_join_di" bpmnElement="sf_pago_join">
        <di:waypoint x="1220" y="640"/>
        <di:waypoint x="1300" y="640"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_join_decano_di" bpmnElement="sf_join_decano">
        <di:waypoint x="1325" y="665"/>
        <di:waypoint x="1325" y="800"/>
        <di:waypoint x="1480" y="800"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="1580" y="800"/>
        <di:waypoint x="1660" y="800"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_final_aprobado_di" bpmnElement="sf_final_aprobado">
        <di:waypoint x="1710" y="800"/>
        <di:waypoint x="1785" y="800"/>
        <di:waypoint x="1785" y="768"/>
        <di:waypoint x="1840" y="768"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_final_rechazado_di" bpmnElement="sf_final_rechazado">
        <di:waypoint x="1710" y="800"/>
        <di:waypoint x="1785" y="800"/>
        <di:waypoint x="1785" y="833"/>
        <di:waypoint x="1840" y="833"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
