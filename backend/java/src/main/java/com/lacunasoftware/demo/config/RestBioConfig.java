package com.lacunasoftware.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lacunasoftware.restpkicore.RestBioService;
import com.lacunasoftware.restpkicore.RestBioServiceFactory;
import com.lacunasoftware.restpkicore.RestPkiOptions;


@Configuration
public class RestBioConfig {

    @Autowired
    private ApplicationProperties properties;

    @Bean
    public RestBioService restBioService() {

        String apiKey = properties.getRestPkiCore().getApiKey();

        // Throw exception if token is not set (this check is here just for the sake of
        // newcomers,
        // you can remove it).
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("API_KEY")) {
            throw new RuntimeException("The API key was not set! Hint: to run this sample " +
                    "you must generate an API key on the REST PKI website and paste it on the " +
                    "file src/main/resources/application.yml");
        }

        String endpoint = properties.getRestPkiCore().getEndpoint();
        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = "https://core.pki.rest/";
        }

        RestPkiOptions options = new RestPkiOptions();
        options.setEndpoint(endpoint);
        options.setApiKey(apiKey);

        return RestBioServiceFactory.getService(options);
    }

}
