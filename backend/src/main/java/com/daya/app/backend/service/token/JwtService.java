package com.daya.app.backend.service.token;

import com.daya.app.backend.entity.User;

import java.util.UUID;

public interface JwtService {

    String generateAccessToken(User user);

    boolean validateToken(String token);

    UUID extractUid(String token);

    String extractSubject(String token);

    long getAccessTokenExpirationSeconds();
}