package com.jairo.workflowtramites.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jairo.workflowtramites.dto.request.ChatAgenteTramitesRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgenteTramitesGatewayService {

    @Value("${agente-tramites.service.url}")
    private String microUrl;

    private final ObjectMapper objectMapper;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public SseEmitter proxyChat(ChatAgenteTramitesRequest request, String authHeader) {
        SseEmitter emitter = new SseEmitter(300_000L);

        final String body;
        try {
            if (request == null) {
                throw new IllegalArgumentException("request es null");
            }
            body = objectMapper.writeValueAsString(request);
            log.info("[gateway-agente-tramites] body a enviar: {}", body);
            log.info("[gateway-agente-tramites] URL micro: {}/agente-tramites/chat", microUrl);
        } catch (Exception e) {
            log.error("[gateway-agente-tramites] error serializando body: {}", e.getMessage(), e);
            try {
                emitter.send(SseEmitter.event().data(
                        Map.of("tipo", "error", "mensaje", "Error serializando: " + e.getMessage())));
            } catch (Exception ignored) {
            }
            emitter.completeWithError(e);
            return emitter;
        }

        executor.submit(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(microUrl + "/agente-tramites/chat"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", authHeader)
                        .header("Accept", "text/event-stream")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .timeout(Duration.ofMinutes(5))
                        .build();

                HttpResponse<Stream<String>> response = httpClient.send(
                        req, HttpResponse.BodyHandlers.ofLines());

                if (response.statusCode() != 200) {
                    emitter.send(SseEmitter.event().data(
                            Map.of("tipo", "error",
                                    "mensaje", "Microservicio respondió " + response.statusCode())));
                    emitter.complete();
                    return;
                }

                log.info("[gateway-agente-tramites] stream iniciado");
                StringBuilder dataBuffer = new StringBuilder();
                Iterator<String> lines = response.body().iterator();
                int eventos = 0;
                while (lines.hasNext()) {
                    String line = lines.next();
                    if (line.isEmpty()) {
                        if (dataBuffer.length() > 0) {
                            String evento = dataBuffer.toString();
                            log.debug("[gateway-agente-tramites] evento #{}: {}", ++eventos, evento);
                            emitter.send(SseEmitter.event().data(evento));
                            dataBuffer.setLength(0);
                        }
                    } else if (line.startsWith("data:")) {
                        String contenido = line.length() > 5
                                ? line.substring(line.charAt(5) == ' ' ? 6 : 5)
                                : "";
                        if (dataBuffer.length() > 0) dataBuffer.append("\n");
                        dataBuffer.append(contenido);
                    }
                }
                if (dataBuffer.length() > 0) {
                    emitter.send(SseEmitter.event().data(dataBuffer.toString()));
                    eventos++;
                }
                log.info("[gateway-agente-tramites] stream cerrado. Total eventos: {}", eventos);
                emitter.complete();
            } catch (Exception e) {
                log.error("Error en gateway de agente trámites: {}", e.getMessage(), e);
                try {
                    emitter.send(SseEmitter.event().data(
                            Map.of("tipo", "error", "mensaje", "Gateway error: " + e.getMessage())));
                } catch (Exception ignored) {
                }
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    public String proxyRecuperarSesion(String sesionId, String authHeader) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(microUrl + "/agente-tramites/sesion/" + sesionId))
                .header("Authorization", authHeader)
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            return null;
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException("Microservicio respondió " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }
}
