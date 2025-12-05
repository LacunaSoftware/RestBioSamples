package com.lacunasoftware.demo.config;

import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

    private final RestPkiCoreProperties restPkiCore;

    public ApplicationProperties(RestPkiCoreProperties restPkiCore) {
        this.restPkiCore = restPkiCore;
    }

    public RestPkiCoreProperties getRestPkiCore() {
        return restPkiCore;
    }
}