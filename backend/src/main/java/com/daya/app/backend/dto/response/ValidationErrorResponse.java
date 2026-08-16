package com.daya.app.backend.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(

        boolean success,

        int status,

        String error,

        Map<String, String> validationErrors,

        LocalDateTime timestamp

) {
}