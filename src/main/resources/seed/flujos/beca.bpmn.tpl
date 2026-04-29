<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="process" isExecutable="true">
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:outgoing>sf_start_academica</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_academica" name="Revisi&#243;n acad&#233;mica" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:incoming>sf_start_academica</bpmn:incoming>
      <bpmn:outgoing>sf_academica_fork</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:parallelGateway id="gw_fork" name="Evaluaci&#243;n en paralelo">
      <bpmn:incoming>sf_academica_fork</bpmn:incoming>
      <bpmn:outgoing>sf_fork_bienestar</bpmn:outgoing>
      <bpmn:outgoing>sf_fork_biblioteca</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_bienestar" name="Evaluaci&#243;n socioecon&#243;mica" camunda:candidateGroups="{{DEPTO:Bienestar Estudiantil}}" camunda:formKey="{{FORM:Evaluacion socioeconomica}}">
      <bpmn:incoming>sf_fork_bienestar</bpmn:incoming>
      <bpmn:outgoing>sf_bienestar_join</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_biblioteca" name="Verificaci&#243;n de biblioteca" camunda:candidateGroups="{{DEPTO:Biblioteca}}" camunda:formKey="{{FORM:Verificacion de biblioteca}}">
      <bpmn:incoming>sf_fork_biblioteca</bpmn:incoming>
      <bpmn:outgoing>sf_biblioteca_join</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:parallelGateway id="gw_join">
      <bpmn:incoming>sf_bienestar_join</bpmn:incoming>
      <bpmn:incoming>sf_biblioteca_join</bpmn:incoming>
      <bpmn:outgoing>sf_join_decano</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
      <bpmn:incoming>sf_join_decano</bpmn:incoming>
      <bpmn:outgoing>sf_decano_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_final" name="&#191;Aprueba la beca?">
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
    <bpmn:sequenceFlow id="sf_start_academica" sourceRef="start" targetRef="task_academica"/>
    <bpmn:sequenceFlow id="sf_academica_fork" sourceRef="task_academica" targetRef="gw_fork"/>
    <bpmn:sequenceFlow id="sf_fork_bienestar" sourceRef="gw_fork" targetRef="task_bienestar"/>
    <bpmn:sequenceFlow id="sf_fork_biblioteca" sourceRef="gw_fork" targetRef="task_biblioteca"/>
    <bpmn:sequenceFlow id="sf_bienestar_join" sourceRef="task_bienestar" targetRef="gw_join"/>
    <bpmn:sequenceFlow id="sf_biblioteca_join" sourceRef="task_biblioteca" targetRef="gw_join"/>
    <bpmn:sequenceFlow id="sf_join_decano" sourceRef="gw_join" targetRef="task_decano"/>
    <bpmn:sequenceFlow id="sf_decano_gw" sourceRef="task_decano" targetRef="gw_final"/>
    <bpmn:sequenceFlow id="sf_gw_aprobado" name="Aprobado" sourceRef="gw_final" targetRef="end_aprobado"/>
    <bpmn:sequenceFlow id="sf_gw_rechazado" name="Rechazado" sourceRef="gw_final" targetRef="end_rechazado"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process">
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="152" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_academica_di" bpmnElement="task_academica">
        <dc:Bounds x="240" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_fork_di" bpmnElement="gw_fork">
        <dc:Bounds x="390" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_bienestar_di" bpmnElement="task_bienestar">
        <dc:Bounds x="500" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_biblioteca_di" bpmnElement="task_biblioteca">
        <dc:Bounds x="500" y="260" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_join_di" bpmnElement="gw_join">
        <dc:Bounds x="660" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="760" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="910" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1030" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="1030" y="282" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_academica_di" bpmnElement="sf_start_academica">
        <di:waypoint x="188" y="200"/>
        <di:waypoint x="240" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_academica_fork_di" bpmnElement="sf_academica_fork">
        <di:waypoint x="340" y="200"/>
        <di:waypoint x="390" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_bienestar_di" bpmnElement="sf_fork_bienestar">
        <di:waypoint x="415" y="175"/>
        <di:waypoint x="415" y="120"/>
        <di:waypoint x="500" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_biblioteca_di" bpmnElement="sf_fork_biblioteca">
        <di:waypoint x="415" y="225"/>
        <di:waypoint x="415" y="300"/>
        <di:waypoint x="500" y="300"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_bienestar_join_di" bpmnElement="sf_bienestar_join">
        <di:waypoint x="600" y="120"/>
        <di:waypoint x="685" y="120"/>
        <di:waypoint x="685" y="175"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_biblioteca_join_di" bpmnElement="sf_biblioteca_join">
        <di:waypoint x="600" y="300"/>
        <di:waypoint x="685" y="300"/>
        <di:waypoint x="685" y="225"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_join_decano_di" bpmnElement="sf_join_decano">
        <di:waypoint x="710" y="200"/>
        <di:waypoint x="760" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="860" y="200"/>
        <di:waypoint x="910" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="960" y="200"/>
        <di:waypoint x="1030" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_di" bpmnElement="sf_gw_rechazado">
        <di:waypoint x="935" y="225"/>
        <di:waypoint x="935" y="300"/>
        <di:waypoint x="1030" y="300"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
