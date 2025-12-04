package com.lacunasoftware.demo.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lacunasoftware.restpkicore.*;

@RestController
@RequestMapping("api/bio/liveness")
public class LivenessController {

    @GetMapping()
    public ResponseEntity<String> livenessExample() throws Exception {
        RestPkiOptions restPkiOptions = new RestPkiOptions();

        restPkiOptions.setEndpoint("https://example.core.pki.rest/");
        restPkiOptions.setApiKey("apykey|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        RestBioService service = RestBioServiceFactory.getService(restPkiOptions);

        StartLivenessSessionRequest request = new StartLivenessSessionRequest();

        request.setSubjectIdentifier("f6bf3b8f-726f-4dad-b544-173862dc1223");
        request.setTrustedOrigin("http://localhost:8080/");

        StartBioSessionResponse bioSessionResponse = service.StartLivenessSessionAsync(request);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=utf-8")
                .body(bioSessionResponse.getSessionUrl());
    }

}
