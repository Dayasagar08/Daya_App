package com.daya.app.backend.service.token;

import com.daya.app.backend.config.AppProperties;
import com.daya.app.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final AppProperties appProperties;

    private SecretKey signingKey;

    @PostConstruct
    void initialize() {

        String jwtSecret =
                appProperties
                        .getJwt()
                        .getSecret();

        byte[] keyBytes;

        try {

            keyBytes =
                    io.jsonwebtoken.io.Decoders.BASE64.decode(
                            jwtSecret
                    );

        } catch (IllegalArgumentException exception) {

            throw new IllegalStateException(
                    "app.jwt.secret must be a valid Base64-encoded secret.",
                    exception
            );
        }

        if (keyBytes.length < 32) {

            throw new IllegalStateException(
                    "app.jwt.secret must contain at least 256 bits (32 bytes)."
            );
        }

        signingKey =
                Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        Instant expiration =
                now.plusMillis(
                        appProperties
                                .getJwt()
                                .getAccessTokenExpiration()
                );

        return Jwts.builder()
                .subject(user.getUid().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        try {

            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (JwtException |
                 IllegalArgumentException exception) {

            return false;
        }
    }

    @Override
    public UUID extractUid(String token) {

        Claims claims =
                extractClaims(token);

        String subject =
                claims.getSubject();

        try {

            return UUID.fromString(subject);

        } catch (IllegalArgumentException exception) {

            throw new JwtException(
                    "Invalid user identifier in token."
            );
        }
    }

    @Override
    public String extractSubject(String token) {

        return extractClaims(token)
                .getSubject();
    }

    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public long getAccessTokenExpirationSeconds() {

        return appProperties
                .getJwt()
                .getAccessTokenExpiration() / 1000;
    }
}