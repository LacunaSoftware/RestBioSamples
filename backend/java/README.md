
# RestBio samples for Java

This folder contains **Java sample projects** demonstrating the usage of Lacuna Software's **Biometric APIs/SDKs** in various programming languages and frameworks.

For other languages, please visit the [repository root](https://github.com/LacunaSoftware/RestBioSamples).

## Default sample (Spring Boot)

A sample using **Spring Boot** with Java 21 can be found in the folder [backend/java](backend/java). The application entry point is [DemoApplication.java](backend/java/src/main/java/com/lacunasoftware/demo/DemoApplication.java).

Steps to execute the sample:

1. [Download the project](https://github.com/LacunaSoftware/RestBioSamples/archive/master.zip) or clone the repository:

bash

```bash
   git clone https://github.com/LacunaSoftware/RestBioSamples.git
```

2. Navigate to the Java project folder:

bash

```bash
   cd backend/java
```

3. Build and run the project using Gradle:

bash

```bash
   gradle bootRun
```

4. The application will start on `http://localhost:5078`

## Available Endpoints

### Liveness Session

- **URL:** `GET /api/bio/liveness`
- **Description:** Starts a liveness session and returns a session URL
- **Authentication:** API key and endpoint URL must be configured in the controller
- **Returns:** A URL string to be used in the frontend

To use this endpoint, update the API key and endpoint URL in [LivenessController.java](backend/java/src/main/java/com/lacunasoftware/demo/api/controller/LivenessController.java):

java

```java
restPkiOptions.setEndpoint("https://your-endpoint.core.pki.rest/");
restPkiOptions.setApiKey("your-api-key-here");
```

## Frontend

The application includes a simple HTML interface at `http://localhost:5078/` with a button to trigger the liveness example.

The frontend calls the `/api/bio/liveness` endpoint and displays the returned session URL as a clickable link.

See [index.html](backend/java/src/main/resources/static/index.html) for the implementation.

## Requirements

- **Java 21** or higher
- **Gradle 8.0+**

## Project structure

- `backend/java/src/main/java/com/lacunasoftware/demo/` - Main application code
    - `Controllers/` - REST API endpoints
    - `Configuration/` - Application configuration
    - `DemoApplication.java` - Application entry point
- `backend/java/src/main/resources/` - Application properties and static files
    - `application.json` - Application configuration

## Dependencies

The project uses Spring Boot 4.0.0 with the following key dependencies:

- **Spring Boot 4.0.0** - Latest version with enhanced features
- **Spring Web MVC** - RESTful API development
- **Spring Boot DevTools** - Development tools for faster iteration
- **Lacuna REST PKI Core Client 1.3.0** - Biometric APIs integration

## See also

- [Samples in other programming languages](https://github.com/LacunaSoftware/RestBioSamples)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Lacuna Software](https://www.lacunasoftware.com)