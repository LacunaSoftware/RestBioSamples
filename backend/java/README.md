# RestBio Java Sample

This folder contains a **Java sample project** demonstrating how to use Lacuna Software's **Biometric APIs/SDKs** with Spring Boot.

For samples in other languages, see the [repository root](https://github.com/LacunaSoftware/RestBioSamples).

## Sample Overview

The sample uses **Spring Boot** (Java 21).
The main class is [DemoApplication.java](src/main/java/com/lacunasoftware/demo/DemoApplication.java).

### How to Run

1. **Clone the repository:**
	```bash
	git clone https://github.com/LacunaSoftware/RestBioSamples.git
	```

2. **Navigate to the Java backend folder:**
	```bash
	cd backend/java
	```

3. **Build and run the project with Gradle:**
	```bash
	gradle bootRun
	```

4. The API will be available at [http://localhost:5078](http://localhost:5078).

## Using the Lacuna REST PKI Core Client Library

This sample uses the Lacuna REST PKI Core Client library (RestPkiNGJavaClient) to interact with Lacuna's biometric APIs.
Check the [GitHub repository](https://github.com/LacunaSoftware/RestPkiNGJavaClient) for the latest version and additional usage instructions.

## Frontend

To run a frontend sample, go to the repository root and check the available frontend options in the main [README](/README.md).  
Choose a frontend, follow its instructions, and it will run on port `4200`.

## Requirements

- **Java 21** or newer
- **Gradle 8.0+**

## Project Structure

- `src/main/java/com/lacunasoftware/demo/`
  - `controllers/` – REST API endpoints
  - `configuration/` – Application configuration
  - `DemoApplication.java` – Application entry point
- `src/main/resources/`
  - `application.json` – Application configuration

## Dependencies

Key dependencies:

- **Spring Boot 3.x** – Modern Spring Boot framework
- **Spring Web MVC** – REST API support
- **Spring Boot DevTools** – Developer tools
- **Lacuna REST PKI Core Client** – Biometric APIs integration

> **Note:** Update the version numbers in `build.gradle` as needed.

## See Also

- [Samples in other programming languages](https://github.com/LacunaSoftware/RestBioSamples)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Lacuna Software](https://www.lacunasoftware.com)
