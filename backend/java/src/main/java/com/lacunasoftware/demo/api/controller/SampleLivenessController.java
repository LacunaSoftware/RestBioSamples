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
import com.lacunasoftware.restpkicore.BioSessionResultDataModel;
import com.lacunasoftware.restpkicore.CompleteBioSessionRequest;
import com.lacunasoftware.restpkicore.LivenessSessionStatusModel;
import com.lacunasoftware.restpkicore.RestBioService;
import com.lacunasoftware.restpkicore.StartBioSessionResponse;
import com.lacunasoftware.restpkicore.StartLivenessSessionRequest;

@RestController
@RequestMapping("sample-api/sessions/liveness")
public class SampleLivenessController {

    private final RestBioService service;
    private final ExampleConfigProperties exampleConfig;

    public SampleLivenessController(
        RestBioService restBioService, 
        ExampleConfigProperties exampleConfig
    ) {
        this.service = restBioService;
        this.exampleConfig = exampleConfig;
    }

    @PostMapping
    public ResponseEntity<StartBioSessionResponse> StartLivenessSession(@RequestParam(required = false, defaultValue = "false") Boolean captureIdentificationDocument) throws Exception {

        // This is an example of how to start a liveness session.
        // You must implement your own security measures to ensure that only users
        // you want to have access to this endpoint can call it.

        // The response of the following call contains the session URL that
        // will be loaded in the Widget to start the biometric session.

        StartLivenessSessionRequest request = new StartLivenessSessionRequest();
        request.setTrustedOrigin(exampleConfig.getTrustedOrigin());
        request.setCaptureIdentificationDocument(captureIdentificationDocument);
        request.setSubjectIdentifier("Example");
        // Additional request properties:
        // request.setFaceCaptureProvider(FaceCaptureProviders.FACETECLIVENESS3D);

        StartBioSessionResponse response = service.StartLivenessSession(request);

        // Although not mandatory, you may want to save the SessionId in your database
        // along with your user/session information, as you can use this ID to retrieve
        // the status of the session by later using the GetLivenessSessionStatus()
        // method. But at the end of the session, the Widget will return you a ticket
        // that you can use to retrieve the session status without needing to store
        // the SessionId.

        // Available properties of the response:
        // response.getSessionUrl()  - The URL to be loaded in the Widget to start the biometric session.
        // response.getSessionId()   - The ID of the session.
        // response.getSessionType() - The type of the session (Liveness).

        return ResponseEntity.ok(response);
    }

    @PostMapping("completion")
    public ResponseEntity<LivenessSessionStatusModel> CompleteLivenessSession(@RequestBody CompleteBioSessionRequest request) throws Exception {

        // This is an example of how to complete a liveness session.
        // You must implement your own security measures to ensure that only users
        // you want to have access to this endpoint can call it.

        // By calling the following endpoint, you will get the final status of the
        // biometric session.

        LivenessSessionStatusModel result = service.CompleteLivenessSession(request.getTicket());

        // Available properties of the result:
        // result.getSessionId()                                    - The ID of the session.
        // result.getFaceLivenessStatus().getAttemptCount()         - The number of face liveness attempts the user made.
        // result.getFaceLivenessStatus().isSuccess()               - Whether the user passed the face liveness test or not.

        // If you called the session with captureIdentificationDocument = true,
        // you can also check if the user captured an ID document:
        // if (result.getIdCaptureStatus() != null) {
        //     result.getIdCaptureStatus().isSuccess()              - Whether the user captured an ID document.
        //     result.getIdCaptureStatus().isMatchedFace()          - Whether the face on the ID document matched the user's face.
        // }

        Boolean success = result.isSuccess();

        if (Boolean.TRUE.equals(success)) {
            // The biometric session was successful and the user passed the liveness test.

            if (Boolean.TRUE.equals(result.isResultDataAvailable())) {
                // When the session has ResultDataAvailable = true, it means that
                // you can now retrieve the images captured during the session.

                BioSessionResultDataModel resultData = service.GetSessionResultData(result.getSessionId());

                // Available properties of resultData:
                // resultData.getFaceData().getFaceImage().getContent()              - The image of the user's face captured during the liveness test.
                // resultData.getFaceData().getFaceImage().getContentType()          - The content type of the face image (e.g. "image/jpeg").

                // resultData.getDocumentData().getFrontImage().getContent()         - The image of the front side of the ID document.
                // resultData.getDocumentData().getFrontImage().getContentType()     - The content type of the front side image (e.g. "image/jpeg").

                // resultData.getDocumentData().getFaceCropImage().getContent()      - The cropped face image on the ID document (may be null).
                // resultData.getDocumentData().getFaceCropImage().getContentType()  - The content type of the cropped face image (may be null).

                // resultData.getDocumentData().getBackImage().getContent()          - The image of the back side of the ID document (may be null).
                // resultData.getDocumentData().getBackImage().getContentType()      - The content type of the back side image (may be null).
            }

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
    public ResponseEntity<BioSessionResultDataModel> GetLivenessSessionStatus(@RequestParam UUID sessionId) throws Exception {
        var status = service.GetSessionResultData(sessionId);
        return ResponseEntity.ok(status);
    }

}