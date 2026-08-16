package com.daya.app.backend.entity;

public enum AccountStatus {

    /**
     * Account is active and can log in.
     */
    ACTIVE,

    /**
     * Registration completed but email verification is pending.
     */
    PENDING_VERIFICATION,

    /**
     * Account is temporarily locked due to multiple failed login attempts.
     */
    LOCKED,

    /**
     * Account has been disabled by an administrator.
     */
    DISABLED,

    /**
     * Account has been suspended due to policy violations.
     */
    SUSPENDED,

    /**
     * Account has been permanently deleted or deactivated.
     */
    DELETED
}