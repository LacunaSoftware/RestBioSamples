package com.lacunasoftware.demo.api.dto;

import jakarta.validation.constraints.NotBlank;

public class SubjectIdentifierDTO {

    @NotBlank(message = "Subject identifier must not be blank")
    private String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
