package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.request.ChatAgenteTramitesRequest;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import com.jairo.workflowtramites.service.AgenteTramitesGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/agente-tramites")
@RequiredArgsConstructor
public class AgenteTramitesController {

    private final AgenteTramitesGatewayService gatewayService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(
            @Valid @RequestBody ChatAgenteTramitesRequest request,
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return gatewayService.proxyChat(request, authHeader);
    }

    @GetMapping("/sesion/{sesionId}")
    public ResponseEntity<String> recuperarSesion(
            @PathVariable String sesionId,
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal AuthenticatedUser user) throws Exception {
        autorizar(user);
        String body = gatewayService.proxyRecuperarSesion(sesionId, authHeader);
        if (body == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private void autorizar(AuthenticatedUser user) {
        if (user == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }
        if (user.getRol() != Rol.SOLICITANTE) {
            throw new AccessDeniedException("Solo solicitantes pueden usar este agente");
        }
    }
}
