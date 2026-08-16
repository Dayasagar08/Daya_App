package com.daya.app.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank
        String oldPassword,

        @NotBlank
        @Size(min = 5, max = 100)
        String newPassword

) {
}