package com.daya.app.backend.dto.response;

import java.time.LocalDateTime;

public record OtpResponse(

        boolean success,

        String message,

        LocalDateTime expiresAt

) {
}