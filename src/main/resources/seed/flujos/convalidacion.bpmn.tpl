<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="process" isExecutable="true">
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:outgoing>sf_start_revision</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_revision" name="Revisi&#243;n de documentos" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:incoming>sf_start_revision</bpmn:incoming>
      <bpmn:outgoing>sf_revision_direccion</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_direccion" name="Aprobaci&#243;n de direcci&#243;n" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
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
      <bpmn:incoming>sf_gw_observado</bpmn:incoming>
      <bpmn:outgoing>sf_correccion_direccion</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_pago" name="Validaci&#243;n de pagos" camunda:candidateGroups="{{DEPTO:Tesoreria}}" camunda:formKey="{{FORM:Validacion de pagos}}">
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
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="132" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_revision_di" bpmnElement="task_revision">
        <dc:Bounds x="220" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_direccion_di" bpmnElement="task_direccion">
        <dc:Bounds x="380" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_decision_di" bpmnElement="gw_decision" isMarkerVisible="true">
        <dc:Bounds x="525" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_pago_di" bpmnElement="task_pago">
        <dc:Bounds x="630" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="790" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="790" y="302" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_correccion_di" bpmnElement="task_correccion">
        <dc:Bounds x="380" y="380" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_revision_di" bpmnElement="sf_start_revision">
        <di:waypoint x="168" y="200"/>
        <di:waypoint x="220" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_revision_direccion_di" bpmnElement="sf_revision_direccion">
        <di:waypoint x="320" y="200"/>
        <di:waypoint x="380" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_direccion_gw_di" bpmnElement="sf_direccion_gw">
        <di:waypoint x="480" y="200"/>
        <di:waypoint x="525" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="575" y="200"/>
        <di:waypoint x="630" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_observado_di" bpmnElement="sf_gw_observado">
        <di:waypoint x="550" y="225"/>
        <di:waypoint x="550" y="340"/>
        <di:waypoint x="430" y="340"/>
        <di:waypoint x="430" y="380"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_di" bpmnElement="sf_gw_rechazado">
        <di:waypoint x="550" y="225"/>
        <di:waypoint x="550" y="320"/>
        <di:waypoint x="790" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_correccion_direccion_di" bpmnElement="sf_correccion_direccion">
        <di:waypoint x="430" y="380"/>
        <di:waypoint x="430" y="240"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_pago_end_di" bpmnElement="sf_pago_end">
        <di:waypoint x="730" y="200"/>
        <di:waypoint x="790" y="200"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
