package com.lacunasoftware.demo.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RestPkiCoreProperties {
    private String endpoint;
    private String apiKey;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String baseUrl) {
        this.endpoint = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String accessToken) {
        this.apiKey = accessToken;
    }
}
