<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="process" isExecutable="true">
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:outgoing>sf_start_revision</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_revision" name="Revisi&#243;n de propuesta" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:incoming>sf_start_revision</bpmn:incoming>
      <bpmn:outgoing>sf_revision_direccion</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_direccion" name="Aprobaci&#243;n de direcci&#243;n" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
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
      <bpmn:incoming>sf_gw_observado</bpmn:incoming>
      <bpmn:outgoing>sf_correccion_direccion</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_comite" name="Evaluaci&#243;n del comit&#233;" camunda:candidateGroups="{{DEPTO:Investigacion}}" camunda:formKey="{{FORM:Aprobacion de investigacion}}">
      <bpmn:incoming>sf_gw_comite</bpmn:incoming>
      <bpmn:outgoing>sf_comite_gw</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:exclusiveGateway id="gw_comite" name="&#191;Comit&#233; aprueba?">
      <bpmn:incoming>sf_comite_gw</bpmn:incoming>
      <bpmn:outgoing>sf_comite_decano</bpmn:outgoing>
      <bpmn:outgoing>sf_comite_rechazado</bpmn:outgoing>
    </bpmn:exclusiveGateway>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
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
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="82" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_revision_di" bpmnElement="task_revision">
        <dc:Bounds x="170" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_direccion_di" bpmnElement="task_direccion">
        <dc:Bounds x="320" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_direccion_di" bpmnElement="gw_direccion" isMarkerVisible="true">
        <dc:Bounds x="465" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_comite_di" bpmnElement="task_comite">
        <dc:Bounds x="570" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_comite_di" bpmnElement="gw_comite" isMarkerVisible="true">
        <dc:Bounds x="715" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="820" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="965" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1080" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="1080" y="342" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_correccion_di" bpmnElement="task_correccion">
        <dc:Bounds x="320" y="380" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_revision_di" bpmnElement="sf_start_revision">
        <di:waypoint x="118" y="200"/>
        <di:waypoint x="170" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_revision_direccion_di" bpmnElement="sf_revision_direccion">
        <di:waypoint x="270" y="200"/>
        <di:waypoint x="320" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_direccion_gw_di" bpmnElement="sf_direccion_gw">
        <di:waypoint x="420" y="200"/>
        <di:waypoint x="465" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_comite_di" bpmnElement="sf_gw_comite">
        <di:waypoint x="515" y="200"/>
        <di:waypoint x="570" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_observado_di" bpmnElement="sf_gw_observado">
        <di:waypoint x="490" y="225"/>
        <di:waypoint x="490" y="340"/>
        <di:waypoint x="370" y="340"/>
        <di:waypoint x="370" y="380"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_inicial_di" bpmnElement="sf_gw_rechazado_inicial">
        <di:waypoint x="490" y="225"/>
        <di:waypoint x="490" y="360"/>
        <di:waypoint x="1080" y="360"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_correccion_direccion_di" bpmnElement="sf_correccion_direccion">
        <di:waypoint x="370" y="380"/>
        <di:waypoint x="370" y="240"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_comite_gw_di" bpmnElement="sf_comite_gw">
        <di:waypoint x="670" y="200"/>
        <di:waypoint x="715" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_comite_decano_di" bpmnElement="sf_comite_decano">
        <di:waypoint x="765" y="200"/>
        <di:waypoint x="820" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_comite_rechazado_di" bpmnElement="sf_comite_rechazado">
        <di:waypoint x="740" y="225"/>
        <di:waypoint x="740" y="360"/>
        <di:waypoint x="1080" y="360"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="920" y="200"/>
        <di:waypoint x="965" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="1015" y="200"/>
        <di:waypoint x="1080" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_final_di" bpmnElement="sf_gw_rechazado_final">
        <di:waypoint x="990" y="225"/>
        <di:waypoint x="990" y="360"/>
        <di:waypoint x="1080" y="360"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
