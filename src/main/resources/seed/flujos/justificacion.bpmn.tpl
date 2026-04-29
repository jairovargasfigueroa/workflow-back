<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="process" isExecutable="true">
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:outgoing>sf_start_revision</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_revision" name="Revisi&#243;n de justificativo" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:incoming>sf_start_revision</bpmn:incoming>
      <bpmn:outgoing>sf_revision_decano</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
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
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="152" y="172" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_revision_di" bpmnElement="task_revision">
        <dc:Bounds x="240" y="150" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="400" y="150" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="560" y="165" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="680" y="172" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="680" y="292" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_revision_di" bpmnElement="sf_start_revision">
        <di:waypoint x="188" y="190"/>
        <di:waypoint x="240" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_revision_decano_di" bpmnElement="sf_revision_decano">
        <di:waypoint x="340" y="190"/>
        <di:waypoint x="400" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="500" y="190"/>
        <di:waypoint x="560" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="610" y="190"/>
        <di:waypoint x="680" y="190"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_di" bpmnElement="sf_gw_rechazado">
        <di:waypoint x="585" y="215"/>
        <di:waypoint x="585" y="310"/>
        <di:waypoint x="680" y="310"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
