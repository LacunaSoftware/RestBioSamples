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
import com.lacunasoftware.restpkicore.BioEnrollmentSessionStatusModel;
import com.lacunasoftware.restpkicore.CompleteBioSessionRequest;
import com.lacunasoftware.restpkicore.RestBioService;
import com.lacunasoftware.restpkicore.StartBioEnrollmentSessionRequest;
import com.lacunasoftware.restpkicore.StartBioSessionResponse;

@RestController
@RequestMapping("sample-api/sessions/enrollment")
public class SampleEnrollmentController {

	private final RestBioService service;
	private final ExampleConfigProperties exampleConfig;

	public SampleEnrollmentController(
		RestBioService restBioService,
		ExampleConfigProperties exampleConfig
	) {
		this.service = restBioService;
		this.exampleConfig = exampleConfig;
	}

	@PostMapping
	public ResponseEntity<StartBioSessionResponse> StartEnrollmentSession(
			@RequestParam String subjectIdentifier,
			@RequestParam(required = false, defaultValue = "false") Boolean captureIdentificationDocument,
			@RequestParam(required = false, defaultValue = "false") Boolean dangerousOverrideIfAlreadyEnrolled)
		throws Exception {

		// This is an example of how to start an enrollment session.
		// You must implement your own security measures to ensure that only users
		// you want to have access to this endpoint can call it.

		// The response of the following call contains the session URL that
		// will be loaded in the Widget to start the biometric session.

		StartBioEnrollmentSessionRequest request = new StartBioEnrollmentSessionRequest();
		request.setSubjectIdentifier(subjectIdentifier);
		request.setTrustedOrigin(exampleConfig.getTrustedOrigin());
		// Additional properties for enrollment:
		request.setCaptureIdentificationDocument(captureIdentificationDocument);
		request.setDangerousOverrideIfAlreadyEnrolled(dangerousOverrideIfAlreadyEnrolled);

		StartBioSessionResponse response = service.StartEnrollmentSession(request);

		// Although not mandatory, you may want to save the SessionId in your database
		// along with your user/session information, as you can use this ID to retrieve
		// the status of the session by later using the GetEnrollmentSessionStatus()
		// method. But at the end of the session, the Widget will return you a ticket
		// that you can use to retrieve the session status without needing to store
		// the SessionId.

		// Available properties of the response:
		// response.getSessionUrl()  - The URL to be loaded in the Widget to start the biometric session.
		// response.getSessionId()   - The ID of the session.
		// response.getSessionType() - The type of the session (Enrollment).

		return ResponseEntity.ok(response);
	}

	@PostMapping("completion")
	public ResponseEntity<BioEnrollmentSessionStatusModel> CompleteEnrollmentSession(@RequestBody CompleteBioSessionRequest request) throws Exception {

		// This is an example of how to complete an enrollment session.
		// You must implement your own security measures to ensure that only users
		// you want to have access to this endpoint can call it.

		// By calling the following endpoint, you will get the final status of the
		// biometric session.

		BioEnrollmentSessionStatusModel result = service.CompleteEnrollmentSession(request.getTicket());

		// Available properties of the result:
		// result.getSessionId()  - The ID of the session.
		// result.isSuccess()     - Whether the biometric session was successful or not.

		Boolean success = result.isSuccess();

		if (Boolean.TRUE.equals(success)) {
			// The biometric session was successful and the user was enrolled.

			// You may want to store the SubjectId to be used later.
			// result.getSubjectId()

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
	public ResponseEntity<BioEnrollmentSessionStatusModel> GetEnrollmentSessionStatus(@RequestParam UUID sessionId) throws Exception {
		BioEnrollmentSessionStatusModel status = service.GetEnrollmentSessionStatus(sessionId);
		return ResponseEntity.ok(status);
	}

}
