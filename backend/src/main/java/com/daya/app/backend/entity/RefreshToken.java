package com.daya.app.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token", columnList = "token", unique = true),
                @Index(name = "idx_refresh_user", columnList = "user_id"),
                @Index(name = "idx_refresh_expiry", columnList = "expires_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseEntity {

    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "token_id", nullable = false, unique = true)
    private UUID tokenId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "status", nullable = false)
    private TokenStatus status;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "platform", length = 50)
    private String platform;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//     @Column(name = "refresh_count")
//     @Builder.Default
//     private Integer refreshCount = 0;
}