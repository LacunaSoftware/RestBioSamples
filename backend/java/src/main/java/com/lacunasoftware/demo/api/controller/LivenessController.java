package com.lacunasoftware.demo.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lacunasoftware.demo.Util;
import com.lacunasoftware.restpkicore.*;

@RestController
@RequestMapping("api/bio/sessions")
public class LivenessController {

    private RestBioService getService() {
		return RestBioServiceFactory.getService(Util.getRestPkiCoreOptions());
    }

    @GetMapping("liveness")
    public ResponseEntity<StartBioSessionResponse> livenessExample() throws Exception {
        RestBioService service = getService();

        StartLivenessSessionRequest request = new StartLivenessSessionRequest();
        request.setSubjectIdentifier("f6bf3b8f-726f-4dad-b544-173862dc1223");
        request.setTrustedOrigin("http://localhost:4200/");

        StartBioSessionResponse bioSessionResponse = service.StartLivenessSessionAsync(request);
        return ResponseEntity.ok(bioSessionResponse);
    }

    @GetMapping("liveness/status")
    public ResponseEntity<LivenessSessionStatusModel> livenessSessionStatus(@RequestParam UUID sessionId) throws Exception {
        RestBioService service = getService();
        LivenessSessionStatusModel status = service.GetLivenessSessionStatusAsync(sessionId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("liveness/completion")
    public ResponseEntity<LivenessSessionStatusModel> completeLivenessSession(@RequestParam String ticket) throws Exception {
        RestBioService service = getService();
        LivenessSessionStatusModel result = service.CompleteLivenessSessionAsync(ticket);
        return ResponseEntity.ok(result);
    }

}
