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
import com.lacunasoftware.demo.api.dto.StartEnrollmentRequestDTO;
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
    public ResponseEntity<StartBioSessionResponse> enrollmentExample(@RequestBody StartEnrollmentRequestDTO requestDTO) throws Exception {

        if (requestDTO.getSubjectIdentifier() == null || requestDTO.getSubjectIdentifier().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        RestBioService service = getService();
        StartBioEnrollmentSessionRequest request = new StartBioEnrollmentSessionRequest();

        request.setSubjectIdentifier(requestDTO.getSubjectIdentifier().trim());
        request.setTrustedOrigin("http://localhost:4200/");

        if (requestDTO.getCaptureIdentificationDocument() != null) {
            request.setCaptureIdentificationDocument(requestDTO.getCaptureIdentificationDocument());
        }

        if (requestDTO.getDangerousOverrideIfAlreadyEnrolled() != null) {
            request.setDangerousOverrideIfAlreadyEnrolled(requestDTO.getDangerousOverrideIfAlreadyEnrolled());
        }

        StartBioSessionResponse bioEnrollmentSessionResponse = service.StartEnrollmentSessionAsync(request);
        return ResponseEntity.ok(bioEnrollmentSessionResponse);
    }

    @GetMapping("enrollment/status")
    public ResponseEntity<BioEnrollmentSessionStatusModel> startEnrollmentSession(@RequestParam UUID sessionId) throws Exception {
        RestBioService service = getService();
        BioEnrollmentSessionStatusModel status = service.GetEnrollmentSessionStatusAsync(sessionId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("enrollment/completion")
    public ResponseEntity<BioEnrollmentSessionStatusModel> completeLivenessSession(@RequestBody CompleteBioSessionRequest request) throws Exception {
        RestBioService service = getService();
        BioEnrollmentSessionStatusModel result = service.CompleteEnrollmentSessionAsync(request.getTicket());
        return ResponseEntity.ok(result);
    }

}
