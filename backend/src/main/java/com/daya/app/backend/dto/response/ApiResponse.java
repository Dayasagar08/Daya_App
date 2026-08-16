package com.daya.app.backend.dto.response;

import java.time.LocalDateTime;

public record ApiResponse(

        boolean success,

        String message,

        LocalDateTime timestamp

) {
}