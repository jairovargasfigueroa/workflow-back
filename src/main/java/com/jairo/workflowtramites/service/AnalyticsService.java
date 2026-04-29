package com.jairo.workflowtramites.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final RestClient analyticsRestClient;

    public Object obtenerFlujos() {
        return analyticsRestClient.get()
                .uri("/analytics/flujos")
                .retrieve()
                .body(Object.class);
    }

    public Object optimizar(String flujoId) {
        return analyticsRestClient.post()
                .uri("/analytics/optimizar")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("flujo_id", flujoId))
                .retrieve()
                .body(Object.class);
    }

    public Map<?, ?> entrenarModelo() {
        return analyticsRestClient.post()
                .uri("/modelo/entrenar")
                .retrieve()
                .body(Map.class);
    }
}
