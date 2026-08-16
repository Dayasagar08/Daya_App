package com.daya.app.backend.dto.request;

import jakarta.validation.constraints.Email;

public record ChangeAlternateEmailRequest(

        @Email
        String newAlternateEmail

) {
}