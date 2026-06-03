package com.jairo.workflowtramites.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jairo.workflowtramites.dto.request.reportes.ChatReporteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGatewayService {

    @Value("${reportes.service.url}")
    private String microUrl;

    private final ObjectMapper objectMapper;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)  // uvicorn solo HTTP/1.1
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public SseEmitter proxyChat(ChatReporteRequest request, String authHeader) {
        SseEmitter emitter = new SseEmitter(300_000L); // 5 min

        // Serializamos ANTES del submit para capturar el body como String final
        // (evita problemas si el thread del executor accede a request en mal momento)
        final String body;
        try {
            if (request == null) {
                throw new IllegalArgumentException("request es null");
            }
            body = objectMapper.writeValueAsString(request);
            log.info("[gateway-chat] body a enviar al micro: {}", body);
            log.info("[gateway-chat] URL micro: {}/reportes/chat", microUrl);
            log.info("[gateway-chat] auth header presente: {}", authHeader != null && !authHeader.isBlank());
        } catch (Exception e) {
            log.error("[gateway-chat] error serializando body: {}", e.getMessage(), e);
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
                        .uri(URI.create(microUrl + "/reportes/chat"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", authHeader)
                        .header("Accept", "text/event-stream")
                        .POST(HttpRequest.BodyPublishers.ofString(body, java.nio.charset.StandardCharsets.UTF_8))
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

                log.info("[gateway-chat] micro respondió {}, iniciando stream", response.statusCode());
                StringBuilder dataBuffer = new StringBuilder();
                Iterator<String> lines = response.body().iterator();
                int eventosForwardeados = 0;
                while (lines.hasNext()) {
                    String line = lines.next();
                    log.debug("[gateway-chat] línea cruda del micro: '{}'", line);
                    if (line.isEmpty()) {
                        if (dataBuffer.length() > 0) {
                            String evento = dataBuffer.toString();
                            log.info("[gateway-chat] forwardeando evento #{}: {}", ++eventosForwardeados, evento);
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
                    String evento = dataBuffer.toString();
                    log.info("[gateway-chat] forwardeando último evento #{}: {}", ++eventosForwardeados, evento);
                    emitter.send(SseEmitter.event().data(evento));
                }
                log.info("[gateway-chat] stream cerrado. Total eventos forwardeados: {}", eventosForwardeados);
                emitter.complete();
            } catch (Exception e) {
                log.error("Error en gateway de chat: {}", e.getMessage(), e);
                try {
                    emitter.send(SseEmitter.event().data(
                            Map.of("tipo", "error", "mensaje", "Gateway error: " + e.getMessage())));
                } catch (Exception ignored) {
                    // emitter ya cerrado
                }
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    public HttpResponse<byte[]> proxyDescarga(String archivoId, String authHeader) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(microUrl + "/reportes/descargar/" + archivoId))
                .header("Authorization", authHeader)
                .GET()
                .timeout(Duration.ofSeconds(60))
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
    }
}
