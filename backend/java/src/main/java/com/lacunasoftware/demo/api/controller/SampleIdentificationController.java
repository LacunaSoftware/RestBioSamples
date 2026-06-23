package com.lacunasoftware.demo.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lacunasoftware.demo.config.ExampleConfigProperties;
import com.lacunasoftware.restpkicore.BioIdentificationSessionStatusModel;
import com.lacunasoftware.restpkicore.CompleteBioSessionRequest;
import com.lacunasoftware.restpkicore.FaceCaptureProviders;
import com.lacunasoftware.restpkicore.RestBioService;
import com.lacunasoftware.restpkicore.StartBioIdentificationSessionRequest;
import com.lacunasoftware.restpkicore.StartBioSessionResponse;

@RestController
@RequestMapping("sample-api/sessions/identification")
public class SampleIdentificationController {

    private final RestBioService service;
    private final ExampleConfigProperties exampleConfig;

    public SampleIdentificationController(
        RestBioService restBioService,
        ExampleConfigProperties exampleConfig
    ) {
        this.service = restBioService;
        this.exampleConfig = exampleConfig;
    }

    @PostMapping
    public ResponseEntity<StartBioSessionResponse> StartIdentificationSession() throws Exception {

        // This is an example of how to start an identification session.
        // You must implement your own security measures to ensure that only users
        // you want to have access to this endpoint can call it.

        // The response of the following call contains the session URL that
        // will be loaded in the Widget to start the biometric session.

        // For identification sessions, you don't need to pass a subject identifier,
        // as the goal of this session is to identify the user among all enrolled users.

        var request = new StartBioIdentificationSessionRequest();
        request.setTrustedOrigin(exampleConfig.getTrustedOrigin());
        // Additional properties for identification session:
        request.setFaceCaptureProvider(FaceCaptureProviders.FACETECLIVENESS3D);

        // Although not mandatory, you may want to save the SessionId in your database
        // along with your user/session information, as you can use this ID to retrieve
        // the status of the session by later using the
        // GetIdentificationSessionCompletionAsync()
        // method. But at the end of the session, the Widget will return you a ticket
        // that you can use to retrieve the session status without needing to store
        // the SessionId.

        var response = service.StartIdentificationSession(request);

        // Available properties of the response:
        response.getSessionUrl();   // The URL to be loaded in the Widget to start the biometric session.
        response.getSessionId();    // The ID of the session.
        response.getSessionType();  // The type of the session (Identification).

        return ResponseEntity.ok(response);
    }

    @PostMapping("completion")
    public ResponseEntity<BioIdentificationSessionStatusModel> GetIdentificationSessionCompletion(@RequestBody CompleteBioSessionRequest request) throws Exception {
        // This is an example of how to complete an identification session.
        // You must implement your own security measures to ensure that only users
        // you want to have access to this endpoint can call it.

        // By calling the following endpoint, you will get the final status of the
        // biometric session.
        var result = service.CompleteIdentificationSession(request.getTicket());

        // Available properties of the result:
        result.getSessionId();                      // The ID of the session.
        var success = result.isSuccess();           // Whether the biometric session was successful or not.

        if (Boolean.TRUE.equals(success)) {
            // The biometric session was successful and the identification process is complete.
            // You can now trust that the person behind the camera is who they claim to be.

        } else if (Boolean.FALSE.equals(success)) {
            // The biometric session was completed, but the identification failed.
            // Here you may want to retry, or implement other security measures.

        } else {
            // The biometric session is still in progress. This should not happen here,
            // as the Widget will only provide a ticket when the session is finalized.
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("status")
    public ResponseEntity<BioIdentificationSessionStatusModel> GetIdentificationSessionStatus(@RequestParam UUID sessionId) throws Exception {
        var status = service.GetIdentificationSessionStatus(sessionId);
        return ResponseEntity.ok(status);
    }

}
