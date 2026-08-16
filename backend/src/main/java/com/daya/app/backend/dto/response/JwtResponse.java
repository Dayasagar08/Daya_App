package com.daya.app.backend.dto.response;

public record JwtResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        long expiresIn

) {
}