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
import com.lacunasoftware.restpkicore.*;

@RestController
@RequestMapping("api/bio/sessions")
public class LivenessController {

	@Autowired
	private Util util;

    private RestBioService getService() {
		return RestBioServiceFactory.getService(util.getRestPkiCoreOptions());
    }

    @PostMapping("liveness")
    public ResponseEntity<StartBioSessionResponse> livenessExample() throws Exception {
        RestBioService service = getService();

        StartLivenessSessionRequest request = new StartLivenessSessionRequest();

		// Here you can set the subjectIdentifier and trustedOrigin properties
		// according to your application's needs. In this example, we are just
		// setting some static values.
        request.setSubjectIdentifier("f6bf3b8f-726f-4dad-b544-173862dc1223");

		// On our example, all frontends are served from this origin. Change it to your
		// application's origin. (maybe you want to set it dynamically according to the
		// request/client).
        request.setTrustedOrigin("http://localhost:4200/");

        StartBioSessionResponse bioSessionResponse = service.StartLivenessSession(request);
        return ResponseEntity.ok(bioSessionResponse);
    }

    @GetMapping("liveness/status")
    public ResponseEntity<LivenessSessionStatusModel> livenessSessionStatus(@RequestParam UUID sessionId) throws Exception {
        RestBioService service = getService();
        LivenessSessionStatusModel status = service.GetLivenessSessionStatus(sessionId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("liveness/completion")
    public ResponseEntity<LivenessSessionStatusModel> completeLivenessSession(@RequestBody CompleteBioSessionRequest request) throws Exception {
        RestBioService service = getService();
        LivenessSessionStatusModel result = service.CompleteLivenessSession(request.getTicket());
        return ResponseEntity.ok(result);
    }

}
