# RestBio Samples

This repository contains sample projects demonstrating the usage of Lacuna Software's
**Biometric APIs/SDKs** in various programming languages and frameworks.


**To get started, choose a backend programming language:**

* [.NET](backend/dotnet/)


**Then, choose a frontend programming language:**

* [Angular](frontend/angular/)

## Application flow
```mermaid
sequenceDiagram
    participant Frontend as App Frontend
    participant Widget as RestPki Widget
    participant Backend as App Backend
    participant RestBio as RestBio Service

    Frontend->>Backend: Start Session (POST /api/bio/session/{type})
    Backend->>RestBio: Create biometric session
    RestBio-->>Backend: Session URL & ID
    Backend-->>Frontend: sessionUrl, sessionId

    Frontend->>Widget: performBioSession(sessionUrl)
    Widget->>RestBio: Perform biometric capture
    RestBio-->>Widget: Complete ticket
    Widget-->>Frontend: completeTicket

    Frontend->>Backend: Complete Session (POST /api/bio/session/{type}/completion)
    Backend->>RestBio: Complete session with ticket
    RestBio-->>Backend: Session result
    Backend-->>Frontend: result.success, sessionId

    alt result.success === true
        Frontend->>Backend: Get Session Status (GET /api/bio/session/{type}/status?sessionId=...)
        Backend->>RestBio: Get session result data
        RestBio-->>Backend: Detailed session data (images, metadata)
        Backend-->>Frontend: Session status with all data
    end
```

## See also

* [PKI Suite demos](https://demos.lacunasoftware.com/)
* [Documentation](https://docs.lacunasoftware.com/)
* [Lacuna Software website](https://www.lacunasoftware.com/)
