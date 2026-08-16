package com.daya.app.backend.service.Implementation;

import com.daya.app.backend.config.AppProperties;
import com.daya.app.backend.entity.RefreshToken;
import com.daya.app.backend.entity.TokenStatus;
import com.daya.app.backend.entity.User;
import com.daya.app.backend.exception.ApiException;
import com.daya.app.backend.exception.ErrorCode;
import com.daya.app.backend.repo.RefreshTokenRepo;
import com.daya.app.backend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshtokenImpl implements RefreshTokenService {

    private static final int TOKEN_BYTES = 64;

    private final RefreshTokenRepo refreshTokenRepository;

    private final AppProperties appProperties;

    private final SecureRandom secureRandom =
            new SecureRandom();

    @Override
    public String create(User user) {

        String rawToken =
                generateSecureToken();

        long refreshTokenExpiration =
                appProperties
                        .getJwt()
                        .getRefreshTokenExpiration();

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(hashToken(rawToken))
                        .tokenId(UUID.randomUUID())
                        .user(user)
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusSeconds(
                                                refreshTokenExpiration / 1000
                                        )
                        )
                        .lastUsedAt(null)
                        .status(TokenStatus.ACTIVE)
                        .deviceName(null)
                        .platform(null)
                        .ipAddress(null)
                        .userAgent(null)
                        .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Override
    public RefreshToken verify(String rawToken) {

        if (rawToken == null ||
                rawToken.isBlank()) {

            throw new ApiException(
                    ErrorCode.INVALID_TOKEN,
                    "Refresh token is required."
            );
        }

        String tokenHash =
                hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(tokenHash)
                        .orElseThrow(() ->
                                new ApiException(
                                        ErrorCode.INVALID_TOKEN,
                                        "Invalid refresh token."
                                )
                        );

        if (refreshToken.getStatus()
                == TokenStatus.REVOKED) {

            throw new ApiException(
                    ErrorCode.REFRESH_TOKEN_REVOKED,
                    "Refresh token has been revoked."
            );
        }

        if (refreshToken.getStatus()
                == TokenStatus.EXPIRED) {

            throw new ApiException(
                    ErrorCode.REFRESH_TOKEN_EXPIRED,
                    "Refresh token has expired."
            );
        }

        if (LocalDateTime.now()
                .isAfter(refreshToken.getExpiresAt())) {

            refreshToken.setStatus(
                    TokenStatus.EXPIRED
            );

            throw new ApiException(
                    ErrorCode.REFRESH_TOKEN_EXPIRED,
                    "Refresh token has expired."
            );
        }

        return refreshToken;
    }

    @Override
    public String rotate(String rawToken) {

        RefreshToken oldToken =
                verify(rawToken);

        User user =
                oldToken.getUser();

        oldToken.setStatus(
                TokenStatus.REVOKED
        );

        oldToken.setLastUsedAt(
                LocalDateTime.now()
        );

        refreshTokenRepository.save(
                oldToken
        );

        return create(user);
    }

    @Override
    public void revoke(String rawToken) {

        if (rawToken == null ||
                rawToken.isBlank()) {

            return;
        }

        String tokenHash =
                hashToken(rawToken);

        refreshTokenRepository
                .findByToken(tokenHash)
                .ifPresent(refreshToken -> {

                    refreshToken.setStatus(
                            TokenStatus.REVOKED
                    );

                    refreshToken.setLastUsedAt(
                            LocalDateTime.now()
                    );

                    refreshTokenRepository.save(
                            refreshToken
                    );
                });
    }

    @Override
    public void revokeAll(User user) {

        refreshTokenRepository
                .findByUserAndStatus(
                        user,
                        TokenStatus.ACTIVE
                )
                .forEach(refreshToken -> {

                    refreshToken.setStatus(
                            TokenStatus.REVOKED
                    );

                    refreshToken.setLastUsedAt(
                            LocalDateTime.now()
                    );
                });
    }

    private String generateSecureToken() {

        byte[] randomBytes =
                new byte[TOKEN_BYTES];

        secureRandom.nextBytes(
                randomBytes
        );

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        randomBytes
                );
    }

    private String hashToken(
            String rawToken
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            rawToken.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    exception
            );
        }
    }
}