package com.daya.app.backend.exception;

public enum ErrorCode {

    // ==========================
    // User
    // ==========================
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,

    PRIMARY_EMAIL_ALREADY_EXISTS,
    ALTERNATE_EMAIL_ALREADY_EXISTS,

    PHONE_NUMBER_ALREADY_EXISTS,

    ROLE_NOT_FOUND,

    // ==========================
    // Authentication
    // ==========================
    INVALID_CREDENTIALS,
    INVALID_PASSWORD,

    EMAIL_NOT_VERIFIED,
    EMAIL_SEND_FAILED,

    ACCOUNT_LOCKED,
    ACCOUNT_DISABLED,
    ACCOUNT_SUSPENDED,

    // ==========================
    // OTP
    // ==========================
    INVALID_OTP,
    OTP_EXPIRED,
    OTP_ALREADY_USED,
    OTP_LIMIT_EXCEEDED,
    OTP_PURPOSE_NOT_SUPPORTED,

    // ==========================
    // JWT
    // ==========================
    INVALID_TOKEN,
    TOKEN_EXPIRED,
    REFRESH_TOKEN_EXPIRED,
    REFRESH_TOKEN_REVOKED,

    // ==========================
    // Validation
    // ==========================
    VALIDATION_ERROR,

    // ==========================
    // Database
    // ==========================
    DATABASE_ERROR,

    // ==========================
    // Internal
    // ==========================
    INTERNAL_SERVER_ERROR, EMAIL_ALREADY_VERIFIED
}