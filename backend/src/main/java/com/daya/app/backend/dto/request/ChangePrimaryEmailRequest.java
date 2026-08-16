package com.daya.app.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangePrimaryEmailRequest(

        @NotBlank
        @Email
        String newPrimaryEmail

) {
}