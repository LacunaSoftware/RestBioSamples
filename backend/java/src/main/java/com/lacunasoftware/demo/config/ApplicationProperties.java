package com.lacunasoftware.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

	@Autowired
    private RestPkiCoreProperties restPkiCore;

    public RestPkiCoreProperties getRestPkiCore() {
        return restPkiCore;
    }
}