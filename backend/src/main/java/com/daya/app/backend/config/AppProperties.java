package com.daya.app.backend.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();

    private final Cors cors = new Cors();

    public Jwt getJwt() {
        return jwt;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Jwt {

        @NotBlank
        private String secret;

        @Min(60000)
        private long accessTokenExpiration = 900000;

        @Min(300000)
        private long refreshTokenExpiration = 2592000000L;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getAccessTokenExpiration() {
            return accessTokenExpiration;
        }

        public void setAccessTokenExpiration(
                long accessTokenExpiration
        ) {
            this.accessTokenExpiration = accessTokenExpiration;
        }

        public long getRefreshTokenExpiration() {
            return refreshTokenExpiration;
        }

        public void setRefreshTokenExpiration(
                long refreshTokenExpiration
        ) {
            this.refreshTokenExpiration = refreshTokenExpiration;
        }
    }

    public static class Cors {

        private String allowedOrigins =
                "http://localhost:3000,http://localhost:5173";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(
                String allowedOrigins
        ) {
            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedOriginList() {

            return Arrays.stream(
                            allowedOrigins.split(",")
                    )
                    .map(String::trim)
                    .filter(origin -> !origin.isBlank())
                    .toList();
        }
    }
}