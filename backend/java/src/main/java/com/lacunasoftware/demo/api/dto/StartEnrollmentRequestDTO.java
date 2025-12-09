package com.lacunasoftware.demo.api.dto;

public class StartEnrollmentRequestDTO {

    private String subjectIdentifier;
    private Boolean captureIdentificationDocument;
    private Boolean dangerousOverrideIfAlreadyEnrolled;

    public StartEnrollmentRequestDTO() {
    }

    public StartEnrollmentRequestDTO(String subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }

    public String getSubjectIdentifier() {
        return subjectIdentifier;
    }

    public void setSubjectIdentifier(String subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }

    public Boolean getCaptureIdentificationDocument() {
        return captureIdentificationDocument;
    }

    public void setCaptureIdentificationDocument(Boolean captureIdentificationDocument) {
        this.captureIdentificationDocument = captureIdentificationDocument;
    }

    public Boolean getDangerousOverrideIfAlreadyEnrolled() {
        return dangerousOverrideIfAlreadyEnrolled;
    }

    public void setDangerousOverrideIfAlreadyEnrolled(Boolean dangerousOverrideIfAlreadyEnrolled) {
        this.dangerousOverrideIfAlreadyEnrolled = dangerousOverrideIfAlreadyEnrolled;
    }

}
