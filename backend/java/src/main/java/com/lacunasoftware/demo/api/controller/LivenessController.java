package com.lacunasoftware.demo.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lacunasoftware.restpkicore.*;

@RestController
@RequestMapping("api/bio/liveness")
public class LivenessController {

    private RestBioService getService() {
        RestPkiOptions restPkiOptions = new RestPkiOptions();
        restPkiOptions.setEndpoint("https://example.core.pki.rest/");
        restPkiOptions.setApiKey("apykey|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        return RestBioServiceFactory.getService(restPkiOptions);
    }

    @GetMapping()
    public ResponseEntity<StartBioSessionResponse> livenessExample() throws Exception {
        RestBioService service = getService();

        StartLivenessSessionRequest request = new StartLivenessSessionRequest();
        request.setSubjectIdentifier("f6bf3b8f-726f-4dad-b544-173862dc1223");
        request.setTrustedOrigin("http://localhost:8080/");

        StartBioSessionResponse bioSessionResponse = service.StartLivenessSessionAsync(request);
        return ResponseEntity.ok(bioSessionResponse);
    }

    @GetMapping("/status/{sessionId}")
    public ResponseEntity<FaceLivenessSessionStatusInfo> livenessSessionStatus(@PathVariable UUID sessionId) throws Exception {
        RestBioService service = getService();
        LivenessSessionStatusModel status = service.GetLivenessSessionStatusAsync(sessionId);
        return ResponseEntity.ok(status.getFaceLivenessStatus());
    }

    @GetMapping("/complete/{ticket}")
    public ResponseEntity<String> completeLivenessSession(@PathVariable String ticket) throws Exception {
        RestBioService service = getService();
        LivenessSessionStatusModel result = service.CompleteLivenessSessionAsync(ticket);
        return ResponseEntity.ok(result.toString());
    }

}
