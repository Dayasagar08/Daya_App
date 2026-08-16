package com.daya.app.backend.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(

        boolean success,

        int status,

        String error,

        String message,

        String path,

        LocalDateTime timestamp

) {
}