package com.daya.app.backend.entity;

public enum OtpPurpose {

    /**
     * Used during user login after email/password verification.
     */
    LOGIN,

    /**
     * Used during new user registration to verify the primary email.
     */
    REGISTER,

    /**
     * Used when a user requests a password reset.
     */
    RESET_PASSWORD,

    /**
     * Used when changing the primary email address.
     */
    CHANGE_PRIMARY_EMAIL,

    /**
     * Used when adding or changing the alternate email address.
     */
    CHANGE_ALTERNATE_EMAIL,

    VERIFY_PHONE,
    TWO_FACTOR_LOGIN,
    DELETE_ACCOUNT,
    ENABLE_2FA
}