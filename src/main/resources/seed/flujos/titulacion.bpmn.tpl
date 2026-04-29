<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="process" isExecutable="true">
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:outgoing>sf_start_revision</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_revision" name="Revisi&#243;n de documentos" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
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
      <bpmn:incoming>sf_docs_correccion</bpmn:incoming>
      <bpmn:outgoing>sf_correccion_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_subsanado" name="&#191;Subsanado?">
      <bpmn:incoming>sf_correccion_gw</bpmn:incoming>
      <bpmn:outgoing>sf_subsanado_revision</bpmn:outgoing>
      <bpmn:outgoing>sf_cancelado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:userTask id="task_direccion" name="Aprobaci&#243;n de propuesta" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
      <bpmn:incoming>sf_docs_direccion</bpmn:incoming>
      <bpmn:outgoing>sf_direccion_fork</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:parallelGateway id="gw_fork" name="Verificaci&#243;n en paralelo">
      <bpmn:incoming>sf_direccion_fork</bpmn:incoming>
      <bpmn:outgoing>sf_fork_biblioteca</bpmn:outgoing>
      <bpmn:outgoing>sf_fork_pago</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_biblioteca" name="Verificaci&#243;n de biblioteca" camunda:candidateGroups="{{DEPTO:Biblioteca}}" camunda:formKey="{{FORM:Verificacion de biblioteca}}">
      <bpmn:incoming>sf_fork_biblioteca</bpmn:incoming>
      <bpmn:outgoing>sf_biblioteca_join</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_pago" name="Validaci&#243;n de pagos" camunda:candidateGroups="{{DEPTO:Tesoreria}}" camunda:formKey="{{FORM:Validacion de pagos}}">
      <bpmn:incoming>sf_fork_pago</bpmn:incoming>
      <bpmn:outgoing>sf_pago_join</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:parallelGateway id="gw_join">
      <bpmn:incoming>sf_biblioteca_join</bpmn:incoming>
      <bpmn:incoming>sf_pago_join</bpmn:incoming>
      <bpmn:outgoing>sf_join_decano</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
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
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="82" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_revision_di" bpmnElement="task_revision">
        <dc:Bounds x="170" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_docs_di" bpmnElement="gw_docs" isMarkerVisible="true">
        <dc:Bounds x="315" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_direccion_di" bpmnElement="task_direccion">
        <dc:Bounds x="420" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_fork_di" bpmnElement="gw_fork">
        <dc:Bounds x="565" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_biblioteca_di" bpmnElement="task_biblioteca">
        <dc:Bounds x="680" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_pago_di" bpmnElement="task_pago">
        <dc:Bounds x="680" y="260" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_join_di" bpmnElement="gw_join">
        <dc:Bounds x="840" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="945" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="1090" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1210" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="1210" y="282" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_correccion_di" bpmnElement="task_correccion">
        <dc:Bounds x="270" y="400" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_subsanado_di" bpmnElement="gw_subsanado" isMarkerVisible="true">
        <dc:Bounds x="420" y="415" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_cancelado_di" bpmnElement="end_cancelado">
        <dc:Bounds x="525" y="422" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_revision_di" bpmnElement="sf_start_revision">
        <di:waypoint x="118" y="200"/>
        <di:waypoint x="170" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_revision_gw_docs_di" bpmnElement="sf_revision_gw_docs">
        <di:waypoint x="270" y="200"/>
        <di:waypoint x="315" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_docs_direccion_di" bpmnElement="sf_docs_direccion">
        <di:waypoint x="365" y="200"/>
        <di:waypoint x="420" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_docs_correccion_di" bpmnElement="sf_docs_correccion">
        <di:waypoint x="340" y="225"/>
        <di:waypoint x="340" y="400"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_correccion_gw_di" bpmnElement="sf_correccion_gw">
        <di:waypoint x="370" y="440"/>
        <di:waypoint x="420" y="440"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_subsanado_revision_di" bpmnElement="sf_subsanado_revision">
        <di:waypoint x="445" y="415"/>
        <di:waypoint x="445" y="360"/>
        <di:waypoint x="220" y="360"/>
        <di:waypoint x="220" y="240"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_cancelado_di" bpmnElement="sf_cancelado">
        <di:waypoint x="470" y="440"/>
        <di:waypoint x="525" y="440"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_direccion_fork_di" bpmnElement="sf_direccion_fork">
        <di:waypoint x="520" y="200"/>
        <di:waypoint x="565" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_biblioteca_di" bpmnElement="sf_fork_biblioteca">
        <di:waypoint x="590" y="175"/>
        <di:waypoint x="590" y="120"/>
        <di:waypoint x="680" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_pago_di" bpmnElement="sf_fork_pago">
        <di:waypoint x="590" y="225"/>
        <di:waypoint x="590" y="300"/>
        <di:waypoint x="680" y="300"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_biblioteca_join_di" bpmnElement="sf_biblioteca_join">
        <di:waypoint x="780" y="120"/>
        <di:waypoint x="865" y="120"/>
        <di:waypoint x="865" y="175"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_pago_join_di" bpmnElement="sf_pago_join">
        <di:waypoint x="780" y="300"/>
        <di:waypoint x="865" y="300"/>
        <di:waypoint x="865" y="225"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_join_decano_di" bpmnElement="sf_join_decano">
        <di:waypoint x="890" y="200"/>
        <di:waypoint x="945" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="1045" y="200"/>
        <di:waypoint x="1090" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_final_aprobado_di" bpmnElement="sf_final_aprobado">
        <di:waypoint x="1140" y="200"/>
        <di:waypoint x="1210" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_final_rechazado_di" bpmnElement="sf_final_rechazado">
        <di:waypoint x="1115" y="225"/>
        <di:waypoint x="1115" y="300"/>
        <di:waypoint x="1210" y="300"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
