package com.daya.app.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(
            name = "uid",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID uid;

    @Column(
            name = "primary_email",
            nullable = false,
            unique = true,
            length = 150
    )
    private String primaryEmail;

    @Column(
            name = "alternate_email",
            unique = true,
            length = 150
    )
    private String alternateEmail;

    @Column(
            name = "password",
            nullable = false,
            length = 255
    )
    private String password;

    @Column(
            name = "display_name",
            nullable = false,
            length = 100
    )
    private String displayName;

    @Column(
            name = "phone_number",
            unique = true,
            length = 20
    )
    private String phoneNumber;

    @Column(
            name = "primary_email_verified",
            nullable = false
    )
    @Builder.Default
    private Boolean primaryEmailVerified = false;

    @Column(
            name = "alternate_email_verified",
            nullable = false
    )
    @Builder.Default
    private Boolean alternateEmailVerified = false;

    @Column(
            name = "account_status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private AccountStatus accountStatus =
            AccountStatus.PENDING_VERIFICATION;

    @Column(
            name = "failed_login_attempts",
            nullable = false
    )
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void generateUid() {
        if (uid == null) {
            uid = UUID.randomUUID();
        }
    }
    @Column(
        name = "registration_otp_resend_count",
        nullable = false
)
@Builder.Default
private Integer registrationOtpResendCount = 0;
}