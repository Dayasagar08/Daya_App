package com.daya.app.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Display name is required")
        @Size(min = 3, max = 100)
        String displayName,

        @NotBlank(message = "Primary email is required")
        @Email(message = "Invalid primary email")
        String primaryEmail,

        @Email(message = "Invalid alternate email")
        String alternateEmail,

        @NotBlank(message = "Password is required")
        @Size(min = 5, max = 100)
        String password,

        @NotBlank(message = "Confirm password is required")
        @Size(min = 5, max = 100)
        String confirmPassword,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[6-9]\\d{9}$",
                message = "Invalid phone number"
        )
        String phoneNumber

) {
}