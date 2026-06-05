package com.jairo.workflowtramites.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${analytics.service.url}")
    private String analyticsServiceUrl;

    @Value("${reportes.service.url}")
    private String reportesServiceUrl;

    @Value("${agente-tramites.service.url}")
    private String agenteTramitesServiceUrl;

    @Bean
    public RestClient analyticsRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(analyticsServiceUrl)
                .build();
    }

    @Bean
    public RestClient reportesRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(reportesServiceUrl)
                .build();
    }

    @Bean
    public RestClient agenteTramitesRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(agenteTramitesServiceUrl)
                .build();
    }
}
