package com.daya.app.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @NotBlank
        String refreshToken

) {
}