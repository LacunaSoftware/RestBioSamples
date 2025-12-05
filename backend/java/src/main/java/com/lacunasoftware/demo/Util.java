package com.lacunasoftware.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lacunasoftware.demo.config.ApplicationProperties;
import com.lacunasoftware.restpkicore.RestPkiOptions;

@Component
public class Util {

	@Autowired
	private ApplicationProperties properties;

	@Autowired
	public Util(ApplicationProperties properties) {
		this.properties = properties;
	}

	public boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public RestPkiOptions getRestPkiCoreOptions() {

		String apiKey = properties.getRestPkiCore().getApiKey();

		// Throw exception if token is not set (this check is here just for the sake of
		// newcomers,
		// you can remove it).
		if (isNullOrEmpty(apiKey) || apiKey.contains("API_KEY")) {
			throw new RuntimeException("The API key was not set! Hint: to run this sample " +
					"you must generate an API key on the REST PKI website and paste it on the " +
					"file src/main/resources/application.yml");
		}

		String endpoint = properties.getRestPkiCore().getEndpoint();
		if (endpoint == null || endpoint.length() == 0) {
			endpoint = "https://core.pki.rest/";
		}

		RestPkiOptions options = new RestPkiOptions();
		options.setEndpoint(endpoint);
		options.setApiKey(apiKey);

		return options;
	}
}