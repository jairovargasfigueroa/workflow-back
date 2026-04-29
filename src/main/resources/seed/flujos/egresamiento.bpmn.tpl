<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="process" isExecutable="true">
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:outgoing>sf_start_academica</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_academica" name="Revisi&#243;n acad&#233;mica" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:incoming>sf_start_academica</bpmn:incoming>
      <bpmn:outgoing>sf_academica_biblioteca</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_biblioteca" name="Verificaci&#243;n de biblioteca" camunda:candidateGroups="{{DEPTO:Biblioteca}}" camunda:formKey="{{FORM:Verificacion de biblioteca}}">
      <bpmn:incoming>sf_academica_biblioteca</bpmn:incoming>
      <bpmn:outgoing>sf_biblioteca_pago</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_pago" name="Validaci&#243;n de pagos" camunda:candidateGroups="{{DEPTO:Tesoreria}}" camunda:formKey="{{FORM:Validacion de pagos}}">
      <bpmn:incoming>sf_biblioteca_pago</bpmn:incoming>
      <bpmn:outgoing>sf_pago_direccion</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_direccion" name="Aprobaci&#243;n de direcci&#243;n" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
      <bpmn:incoming>sf_pago_direccion</bpmn:incoming>
      <bpmn:outgoing>sf_direccion_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_final" name="&#191;Cumple requisitos?">
      <bpmn:incoming>sf_direccion_gw</bpmn:incoming>
      <bpmn:outgoing>sf_gw_aprobado</bpmn:outgoing>
      <bpmn:outgoing>sf_gw_rechazado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:endEvent id="end_aprobado" name="Aprobado">
      <bpmn:incoming>sf_gw_aprobado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:endEvent id="end_rechazado" name="Rechazado">
      <bpmn:incoming>sf_gw_rechazado</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="sf_start_academica" sourceRef="start" targetRef="task_academica"/>
    <bpmn:sequenceFlow id="sf_academica_biblioteca" sourceRef="task_academica" targetRef="task_biblioteca"/>
    <bpmn:sequenceFlow id="sf_biblioteca_pago" sourceRef="task_biblioteca" targetRef="task_pago"/>
    <bpmn:sequenceFlow id="sf_pago_direccion" sourceRef="task_pago" targetRef="task_direccion"/>
    <bpmn:sequenceFlow id="sf_direccion_gw" sourceRef="task_direccion" targetRef="gw_final"/>
    <bpmn:sequenceFlow id="sf_gw_aprobado" name="Aprobado" sourceRef="gw_final" targetRef="end_aprobado"/>
    <bpmn:sequenceFlow id="sf_gw_rechazado" name="Rechazado" sourceRef="gw_final" targetRef="end_rechazado"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process">
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="152" y="172" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_academica_di" bpmnElement="task_academica">
        <dc:Bounds x="240" y="150" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_biblioteca_di" bpmnElement="task_biblioteca">
        <dc:Bounds x="400" y="150" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_pago_di" bpmnElement="task_pago">
        <dc:Bounds x="560" y="150" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_direccion_di" bpmnElement="task_direccion">
        <dc:Bounds x="720" y="150" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="880" y="165" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1000" y="172" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="1000" y="292" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_academica_di" bpmnElement="sf_start_academica">
        <di:waypoint x="188" y="190"/>
        <di:waypoint x="240" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_academica_biblioteca_di" bpmnElement="sf_academica_biblioteca">
        <di:waypoint x="340" y="190"/>
        <di:waypoint x="400" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_biblioteca_pago_di" bpmnElement="sf_biblioteca_pago">
        <di:waypoint x="500" y="190"/>
        <di:waypoint x="560" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_pago_direccion_di" bpmnElement="sf_pago_direccion">
        <di:waypoint x="660" y="190"/>
        <di:waypoint x="720" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_direccion_gw_di" bpmnElement="sf_direccion_gw">
        <di:waypoint x="820" y="190"/>
        <di:waypoint x="880" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="930" y="190"/>
        <di:waypoint x="1000" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_di" bpmnElement="sf_gw_rechazado">
        <di:waypoint x="905" y="215"/>
        <di:waypoint x="905" y="310"/>
        <di:waypoint x="1000" y="310"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
