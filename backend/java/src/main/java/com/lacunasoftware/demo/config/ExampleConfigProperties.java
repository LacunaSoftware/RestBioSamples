package com.lacunasoftware.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


// This is an example configuration class for the RestBio service.
// You must create your own configuration class and set the properties
// according to your application's requirements.

@ConfigurationProperties(prefix = "application.example")
public class ExampleConfigProperties {

    // Trusted Origin is the URL of your application that will be accesed by the final users.
    // For simplicity, this example uses a fixed URL for the Trusted Origin, however, in a real application
    // you might want to set this value dynamically, for example, by reading it
    // from an environment variable or based on the request's origin.
    // Make sure to only allow URLs that you trust, as the RestBio service will use
    // this URL as a security measure to prevent cross-site iFraming attacks.

    private String trustedOrigin;

    public String getTrustedOrigin() {
        return trustedOrigin;
    }

    public void setTrustedOrigin(String trustedOrigin) {
        this.trustedOrigin = trustedOrigin;
    }

}
