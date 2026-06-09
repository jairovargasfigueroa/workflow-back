package com.jairo.workflowtramites.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${analytics.service.url}")
    private String analyticsServiceUrl;

    @Value("${reportes.service.url}")
    private String reportesServiceUrl;

    @Value("${agente-tramites.service.url}")
    private String agenteTramitesServiceUrl;

    @Value("${motor.service.url}")
    private String motorServiceUrl;

    /**
     * Timeouts para todos los RestClient hacia los micros.
     * SIN esto, si un micro queda colgado (acepta la conexión pero no responde),
     * la llamada espera indefinidamente, NO se lanza excepción y el fallback
     * graceful (disponible=false) del gateway NUNCA se activa -> el request del
     * usuario queda colgado. Con timeout, una respuesta lenta lanza excepción
     * y el gateway degrada elegantemente.
     *
     * connect 5s (conectar al micro) / read 30s (el motor de deep learning puede
     * tardar algo en predecir, pero no debería pasar de eso).
     */
    private ClientHttpRequestFactory factoryConTimeouts() {
        return ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(5))
                        .withReadTimeout(Duration.ofSeconds(30)));
    }

    @Bean
    public RestClient analyticsRestClient(RestClient.Builder builder) {
        return builder
                .requestFactory(factoryConTimeouts())
                .baseUrl(analyticsServiceUrl)
                .build();
    }

    @Bean
    public RestClient reportesRestClient(RestClient.Builder builder) {
        return builder
                .requestFactory(factoryConTimeouts())
                .baseUrl(reportesServiceUrl)
                .build();
    }

    @Bean
    public RestClient agenteTramitesRestClient(RestClient.Builder builder) {
        return builder
                .requestFactory(factoryConTimeouts())
                .baseUrl(agenteTramitesServiceUrl)
                .build();
    }

    @Bean
    public RestClient motorRestClient(RestClient.Builder builder) {
        return builder
                .requestFactory(factoryConTimeouts())
                .baseUrl(motorServiceUrl)
                .build();
    }
}
