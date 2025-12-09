package com.lacunasoftware.demo.api.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lacunasoftware.demo.Util;
import com.lacunasoftware.demo.api.dto.BioSubjectReferenceDTO;
import com.lacunasoftware.restpkicore.*;

@RestController
@RequestMapping("api/bio/sessions/authentication")
public class AuthenticationController {

	@Autowired
	private Util util;

	private RestBioService getService() {
		return RestBioServiceFactory.getService(util.getRestPkiCoreOptions());
	}

	@PostMapping
	public ResponseEntity<StartBioSessionResponse> start(@RequestBody BioSubjectReferenceDTO subject)
			throws Exception {
		if (subject.getIdentifier() == null || subject.getIdentifier().trim().isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		RestBioService service = getService();
		StartBioAuthenticationSessionRequest request = new StartBioAuthenticationSessionRequest();

		String identifierString = subject.getIdentifier().trim();
		BioSubjectModel model = service.GetSubjectByIdentifier(identifierString);

		BioSubjectReference bioSubject = new BioSubjectReference();
		bioSubject.setId(model.getId());
		bioSubject.setIdentifier(model.getIdentifier());

		request.setSubject(bioSubject);
		request.setTrustedOrigin("http://localhost:4200/");

		StartBioSessionResponse bioAuthenticationSessionResponse = service.StartAuthenticationSession(request);
		return ResponseEntity.ok(bioAuthenticationSessionResponse);
	}

	@GetMapping("status")
	public ResponseEntity<BioAuthenticationSessionStatusModel> getStatus(
			@RequestParam UUID sessionId) throws Exception {
		RestBioService service = getService();
		BioAuthenticationSessionStatusModel status = service.GetAuthenticationSessionStatus(sessionId);
		return ResponseEntity.ok(status);
	}

	@PostMapping("completion")
	public ResponseEntity<BioAuthenticationSessionStatusModel> complete(
			@RequestBody CompleteBioSessionRequest request) throws Exception {
		RestBioService service = getService();
		BioAuthenticationSessionStatusModel result = service.CompleteAuthenticationSession(request.getTicket());
		return ResponseEntity.ok(result);
	}

}
