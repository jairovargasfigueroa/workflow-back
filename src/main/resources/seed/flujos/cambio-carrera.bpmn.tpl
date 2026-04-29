<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="process" isExecutable="true">
    <bpmn:startEvent id="start" name="Inicio">
      <bpmn:outgoing>sf_start_academica</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="task_academica" name="Revisi&#243;n acad&#233;mica" camunda:candidateGroups="{{DEPTO:Secretaria Academica}}" camunda:formKey="{{FORM:Revision academica}}">
      <bpmn:incoming>sf_start_academica</bpmn:incoming>
      <bpmn:outgoing>sf_academica_actual</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_carrera_actual" name="Aprobaci&#243;n carrera actual" camunda:candidateGroups="{{DEPTO:Direccion de Carrera}}" camunda:formKey="{{FORM:Aprobacion de direccion de carrera}}">
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
      <bpmn:incoming>sf_fork_nueva</bpmn:incoming>
      <bpmn:outgoing>sf_nueva_join</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="task_pago" name="Validaci&#243;n de pagos" camunda:candidateGroups="{{DEPTO:Tesoreria}}" camunda:formKey="{{FORM:Validacion de pagos}}">
      <bpmn:incoming>sf_fork_pago</bpmn:incoming>
      <bpmn:outgoing>sf_pago_join</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:parallelGateway id="gw_join">
      <bpmn:incoming>sf_nueva_join</bpmn:incoming>
      <bpmn:incoming>sf_pago_join</bpmn:incoming>
      <bpmn:outgoing>sf_join_decano</bpmn:outgoing>
    </bpmn:parallelGateway>
    <bpmn:userTask id="task_decano" name="Dictamen del decano" camunda:candidateGroups="{{DEPTO:Decanato}}" camunda:formKey="{{FORM:Dictamen del decano}}">
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
      <bpmndi:BPMNShape id="start_di" bpmnElement="start">
        <dc:Bounds x="82" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_academica_di" bpmnElement="task_academica">
        <dc:Bounds x="170" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_carrera_actual_di" bpmnElement="task_carrera_actual">
        <dc:Bounds x="320" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_salida_di" bpmnElement="gw_salida" isMarkerVisible="true">
        <dc:Bounds x="465" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_fork_di" bpmnElement="gw_fork">
        <dc:Bounds x="560" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_carrera_nueva_di" bpmnElement="task_carrera_nueva">
        <dc:Bounds x="660" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_pago_di" bpmnElement="task_pago">
        <dc:Bounds x="660" y="260" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_join_di" bpmnElement="gw_join">
        <dc:Bounds x="820" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="task_decano_di" bpmnElement="task_decano">
        <dc:Bounds x="920" y="160" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="gw_final_di" bpmnElement="gw_final" isMarkerVisible="true">
        <dc:Bounds x="1065" y="175" width="50" height="50"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_aprobado_di" bpmnElement="end_aprobado">
        <dc:Bounds x="1180" y="182" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="end_rechazado_di" bpmnElement="end_rechazado">
        <dc:Bounds x="1180" y="302" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="sf_start_academica_di" bpmnElement="sf_start_academica">
        <di:waypoint x="118" y="200"/>
        <di:waypoint x="170" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_academica_actual_di" bpmnElement="sf_academica_actual">
        <di:waypoint x="270" y="200"/>
        <di:waypoint x="320" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_actual_gw_di" bpmnElement="sf_actual_gw">
        <di:waypoint x="420" y="200"/>
        <di:waypoint x="465" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_fork_di" bpmnElement="sf_gw_fork">
        <di:waypoint x="515" y="200"/>
        <di:waypoint x="560" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_inicial_di" bpmnElement="sf_gw_rechazado_inicial">
        <di:waypoint x="490" y="225"/>
        <di:waypoint x="490" y="320"/>
        <di:waypoint x="1180" y="320"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_nueva_di" bpmnElement="sf_fork_nueva">
        <di:waypoint x="585" y="175"/>
        <di:waypoint x="585" y="120"/>
        <di:waypoint x="660" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_fork_pago_di" bpmnElement="sf_fork_pago">
        <di:waypoint x="585" y="225"/>
        <di:waypoint x="585" y="300"/>
        <di:waypoint x="660" y="300"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_nueva_join_di" bpmnElement="sf_nueva_join">
        <di:waypoint x="760" y="120"/>
        <di:waypoint x="845" y="120"/>
        <di:waypoint x="845" y="175"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_pago_join_di" bpmnElement="sf_pago_join">
        <di:waypoint x="760" y="300"/>
        <di:waypoint x="845" y="300"/>
        <di:waypoint x="845" y="225"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_join_decano_di" bpmnElement="sf_join_decano">
        <di:waypoint x="870" y="200"/>
        <di:waypoint x="920" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_decano_gw_di" bpmnElement="sf_decano_gw">
        <di:waypoint x="1020" y="200"/>
        <di:waypoint x="1065" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_aprobado_di" bpmnElement="sf_gw_aprobado">
        <di:waypoint x="1115" y="200"/>
        <di:waypoint x="1180" y="200"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="sf_gw_rechazado_di" bpmnElement="sf_gw_rechazado">
        <di:waypoint x="1090" y="225"/>
        <di:waypoint x="1090" y="320"/>
        <di:waypoint x="1180" y="320"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
