package com.jairo.workflowtramites.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BpmnWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/flujo/{flujoId}")
    public void sincronizarDiagrama(
            @DestinationVariable String flujoId,
            @Payload String xml) {
        messagingTemplate.convertAndSend("/topic/flujo/" + flujoId, xml);
    }
}
