package com.daya.app.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "login_otps",
        indexes = {
                @Index(
                        name = "idx_login_otp_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_login_otp_email",
                        columnList = "email"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOtp extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_login_otp_user"
            )
    )
    private User user;

    @Column(
            nullable = false,
            length = 150
    )
    private String email;

    /*
     * Never store the actual OTP.
     * Only the BCrypt hash is stored.
     */
    @Column(
            name = "otp_hash",
            nullable = false,
            length = 255
    )
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private OtpPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private OtpStatus status = OtpStatus.PENDING;

    @Column(
            name = "otp_sent_at",
            nullable = false
    )
    private LocalDateTime otpSentAt;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            name = "verified_at"
    )
    private LocalDateTime verifiedAt;

    @Column(
            name = "attempts",
            nullable = false
    )
    @Builder.Default
    private Integer attempts = 0;
}