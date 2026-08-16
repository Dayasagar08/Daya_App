package com.daya.app.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendOtpRequest(

        @NotBlank
        @Email
        String email

) {
}