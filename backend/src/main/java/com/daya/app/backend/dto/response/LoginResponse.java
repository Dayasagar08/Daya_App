package com.daya.app.backend.dto.response;

import java.time.LocalDateTime;

public record LoginResponse(

        boolean otpRequired,

        String message,

        LocalDateTime otpExpiresAt

) {
}