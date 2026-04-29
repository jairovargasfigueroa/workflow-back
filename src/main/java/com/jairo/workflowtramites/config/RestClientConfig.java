package com.jairo.workflowtramites.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${analytics.service.url}")
    private String analyticsServiceUrl;

    @Bean
    public RestClient analyticsRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(analyticsServiceUrl)
                .build();
    }
}
