package com.lacunasoftware.demo;

import org.springframework.beans.factory.annotation.Autowired;

import com.lacunasoftware.demo.config.ApplicationProperties;
import com.lacunasoftware.restpkicore.RestPkiOptions;

public class Util {

	private static ApplicationProperties properties;

	@Autowired
	private static void setProperties(ApplicationProperties properties) {
		Util.properties = properties;
	}

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public static RestPkiOptions getRestPkiCoreOptions() {

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