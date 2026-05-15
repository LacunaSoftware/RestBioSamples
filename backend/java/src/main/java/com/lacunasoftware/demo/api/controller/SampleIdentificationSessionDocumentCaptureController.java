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
import com.lacunasoftware.restpkicore.CompleteBioSessionRequest;
import com.lacunasoftware.restpkicore.IdentificationDocumentCaptureSessionStatusModel;
import com.lacunasoftware.restpkicore.RestBioService;
import com.lacunasoftware.restpkicore.RestException;
import com.lacunasoftware.restpkicore.StartBioSessionResponse;
import com.lacunasoftware.restpkicore.StartIdentificationDocumentCaptureSessionRequest;

@RestController
@RequestMapping("sample-api/sessions/identification/document-capture")
public class SampleIdentificationSessionDocumentCaptureController {
    
    private final RestBioService service;
    private final ExampleConfigProperties exampleConfig;

    public SampleIdentificationSessionDocumentCaptureController(
        RestBioService restBioService,
        ExampleConfigProperties exampleConfig
    ) {
        this.service = restBioService;
        this.exampleConfig = exampleConfig;
    }

    @PostMapping()
    public ResponseEntity<StartBioSessionResponse> StartIdentificationSessionDocumentCapture() throws Exception {
        // This is an example of how to start an identification document capture session.
        // You must implement your own security measures to ensure that only users
        // you want to have access to this endpoint can call it.

        // The response of the following call contains the session URL that
        // will be loaded in the Widget to start the biometric session.

        var request = new StartIdentificationDocumentCaptureSessionRequest();
        request.setTrustedOrigin(exampleConfig.getTrustedOrigin());
        request.setSubjectIdentifier("Example");


        var response = service.StartIdentificationDocumentCaptureSession(request);
        // Although not mandatory, you may want to save the SessionId in your database
        // along with your user/session information, as you can use this ID to retrieve
        // the status of the session by later using the GetIdentificationDocumentCaptureSessionStatusAsync()
        // method. But at the end of the session, the Widget will return you a ticket
        // that you can use to retrieve the session status without needing to store
        // the SessionId.

        // Available properties of the response:
        response.getSessionUrl();       // The URL to be loaded in the Widget to start the biometric session.
        response.getSessionId();        // The ID of the session.
        response.getSessionType();      // The type of the session (IdentificationDocumentCapture).

		return ResponseEntity.ok(response);

    }

    @PostMapping("completion")
    public ResponseEntity<IdentificationDocumentCaptureSessionStatusModel> GetIdentificationSessionDocumentCaptureCompletion(@RequestBody CompleteBioSessionRequest request) throws RestException {
        
        // This is an example of how to complete an identification document session.
        // You must implement your own security measures to ensure that only users
        // you want to have access to this endpoint can call it.

        // By calling the following endpoint, you will get the final status of the
        // biometric session.

         var result = service.CompleteIdentificationDocumentCaptureSession(request.getTicket());

        // Available properties of the result:
        result.getSessionId();                      // The ID of the session.
        var success = result.isSuccess();           // Whether the biometric session was successful or not.

        if (Boolean.TRUE.equals(success)) {
            // The biometric session was successful and the user captured an ID document that passed all checks.

            // You may want to store the ID capture status to be used later
            result.getIdCaptureStatus();            // Whether the user captured an ID document.

        } else if (Boolean.FALSE.equals(success)) {
            // The biometric session was completed, but it was not successful.
            // Here you may want to retry or increase an attempt counter of the user.

        } else {
            // The biometric session is still in progress. This should not happen here,
            // as the Widget will only provide a complete ticket when the session is completed
            // (either successfully or not).
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("status")
    public ResponseEntity<IdentificationDocumentCaptureSessionStatusModel> GetLivenessSessionStatus(@RequestParam UUID sessionId) throws Exception {
        var status = service.GetIdentificationDocumentCaptureSessionStatus(sessionId);
        return ResponseEntity.ok(status);
    }

}
