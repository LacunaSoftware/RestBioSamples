package com.lacunasoftware.demo.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lacunasoftware.demo.config.ExampleConfigProperties;
import com.lacunasoftware.restpkicore.BioAuthenticationSessionStatusModel;
import com.lacunasoftware.restpkicore.BioSubjectReference;
import com.lacunasoftware.restpkicore.CompleteBioSessionRequest;
import com.lacunasoftware.restpkicore.RestBioService;
import com.lacunasoftware.restpkicore.StartBioAuthenticationSessionRequest;
import com.lacunasoftware.restpkicore.StartBioSessionResponse;

@RestController
@RequestMapping("sample-api/sessions/authentication")
public class SampleAuthenticationController {

	private final RestBioService service;
	private final ExampleConfigProperties exampleConfig;

	public SampleAuthenticationController(
		RestBioService restBioService,
		ExampleConfigProperties exampleConfig
	) {
		this.service = restBioService;
		this.exampleConfig = exampleConfig;
	}

	@PostMapping()
	public ResponseEntity<StartBioSessionResponse> StartAuthenticationSession(@ModelAttribute BioSubjectReference subject) throws Exception {

		// This is an example of how to start an authentication session.
		// You must implement your own security measures to ensure that only users
		// you want to have access to this endpoint can call it.

		// The response of the following call contains the session URL that
		// will be loaded in the Widget to start the biometric session.

		StartBioAuthenticationSessionRequest request = new StartBioAuthenticationSessionRequest();
		request.setSubject(subject);
		request.setTrustedOrigin(exampleConfig.getTrustedOrigin());
		// Additional options for the authentication session:
		// request.set(...)

		StartBioSessionResponse response = service.StartAuthenticationSession(request);

		// Although not mandatory, you may want to save the SessionId in your database
		// along with your user/session information, as you can use this ID to retrieve
		// the status of the session by later using the GetAuthenticationSessionStatus()
		// method. But at the end of the session, the Widget will return you a ticket
		// that you can use to retrieve the session status without needing to store
		// the SessionId.

		// Available properties of the response:
		// response.getSessionUrl()  - The URL to be loaded in the Widget to start the biometric session.
		// response.getSessionId()   - The ID of the session.
		// response.getSessionType() - The type of the session (Authentication).

		return ResponseEntity.ok(response);
	}

	@PostMapping("completion")
	public ResponseEntity<BioAuthenticationSessionStatusModel> CompleteAuthenticationSession(@RequestBody CompleteBioSessionRequest request) throws Exception {

		// This is an example of how to complete an authentication session.
		// You must implement your own security measures to ensure that only users
		// you want to have access to this endpoint can call it.

		// By calling the following endpoint, you will get the final status of the
		// biometric session.

		BioAuthenticationSessionStatusModel result = service.CompleteAuthenticationSession(request.getTicket());

		// Available properties of the result:
		// result.getSessionId() - The ID of the session.
		// result.isSuccess()    - Whether the biometric session was successful or not.

		Boolean success = result.isSuccess();

		if (Boolean.TRUE.equals(success)) {
			// The biometric session was successful and the user was authenticated.

		} else if (Boolean.FALSE.equals(success)) {
			// The biometric session was completed, but authentication failed.
			// Here you may want to retry, lock the account, or implement other security measures.

		} else {
			// The biometric session is still in progress. This should not happen here,
			// as the Widget will only provide a complete ticket when the session is completed
			// (either successfully or not).
		}

		return ResponseEntity.ok(result);
	}

	@GetMapping("status")
	public ResponseEntity<BioAuthenticationSessionStatusModel> GetAuthenticationSessionStatus(@RequestParam UUID sessionId) throws Exception {
		BioAuthenticationSessionStatusModel status = service.GetAuthenticationSessionStatus(sessionId);
		return ResponseEntity.ok(status);
	}

}
