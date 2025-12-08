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
public class EnrollmentController {

    @Autowired
    private Util util;

    private RestBioService getService() {
        return RestBioServiceFactory.getService(util.getRestPkiCoreOptions());
    }

    @PostMapping("enrollment")
    public ResponseEntity<StartBioSessionResponse> enrollmentExample() throws Exception {
        RestBioService service = getService();
        StartBioEnrollmentSessionRequest request = new StartBioEnrollmentSessionRequest();

        request.setSubjectIdentifier(null);

        // Here you can set the subjectIdentifier and trustedOrigin properties
        // according to your application's needs. In this example, we are just
        // setting some static values.
        request.setSubjectIdentifier("f6bf3b8f-726f-4dad-b544-173862dc1223");

        // On our example, all frontends are served from this origin. Change it to your
        // application's origin. (maybe you want to set it dynamically according to the
        // request/client).
        request.setTrustedOrigin("http://localhost:4200/");

        StartBioSessionResponse bioEnrollmentSessionResponse = service.StartEnrollmentSessionAsync(request);

        return ResponseEntity.ok(bioEnrollmentSessionResponse);
    }

    @GetMapping("enrollment/status")
    public ResponseEntity<BioEnrollmentSessionStatusModel> startEnrollmentSession(@RequestParam UUID sessionId)
            throws Exception {
        RestBioService service = getService();
        BioEnrollmentSessionStatusModel status = service.GetEnrollmentSessionStatusAsync(sessionId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("enrollment/completion")
    public ResponseEntity<BioEnrollmentSessionStatusModel> completeLivenessSession(
            @RequestBody CompleteBioSessionRequest request) throws Exception {
        RestBioService service = getService();
        BioEnrollmentSessionStatusModel result = service.CompleteEnrollmentSessionAsync(request.getTicket());
        return ResponseEntity.ok(result);
    }

}
