package com.daya.app.backend.exception;

import com.daya.app.backend.dto.response.ErrorResponse;
import com.daya.app.backend.dto.response.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            ApiException exception,
            HttpServletRequest request
    ) {

        HttpStatus status = getHttpStatus(exception.getErrorCode());

        ErrorResponse response = new ErrorResponse(
                false,
                status.value(),
                exception.getErrorCode().name(),
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ValidationErrorResponse response = new ValidationErrorResponse(
                false,
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR.name(),
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {

        ErrorResponse response = new ErrorResponse(
                false,
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR.name(),
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {

        ErrorResponse response = new ErrorResponse(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                "An unexpected error occurred.",
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    private HttpStatus getHttpStatus(ErrorCode errorCode) {

        return switch (errorCode) {

            case USER_NOT_FOUND,
                 ROLE_NOT_FOUND ->
                    HttpStatus.NOT_FOUND;

            case USER_ALREADY_EXISTS,
                 PRIMARY_EMAIL_ALREADY_EXISTS,
                 ALTERNATE_EMAIL_ALREADY_EXISTS,
                 PHONE_NUMBER_ALREADY_EXISTS ->
                    HttpStatus.CONFLICT;

            case INVALID_CREDENTIALS,
                 INVALID_PASSWORD,
                 INVALID_OTP,
                 OTP_EXPIRED,
                 OTP_ALREADY_USED,
                 INVALID_TOKEN,
                 TOKEN_EXPIRED,
                 REFRESH_TOKEN_EXPIRED,
                 REFRESH_TOKEN_REVOKED ->
                    HttpStatus.UNAUTHORIZED;

            case ACCOUNT_LOCKED ->
                    HttpStatus.LOCKED;

            case ACCOUNT_DISABLED,
                 ACCOUNT_SUSPENDED,
                 EMAIL_NOT_VERIFIED ->
                    HttpStatus.FORBIDDEN;

            case OTP_LIMIT_EXCEEDED ->
                    HttpStatus.TOO_MANY_REQUESTS;

            case VALIDATION_ERROR ->
                    HttpStatus.BAD_REQUEST;

            case DATABASE_ERROR ->
                    HttpStatus.INTERNAL_SERVER_ERROR;

            default ->
                    HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}