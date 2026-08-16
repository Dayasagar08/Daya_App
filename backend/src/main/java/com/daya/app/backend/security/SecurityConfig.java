package com.daya.app.backend.security;

import com.daya.app.backend.config.AppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthFilter jwtAuthenticationFilter;

    private final AppProperties appProperties;

    public SecurityConfig(
            JWTAuthFilter jwtAuthenticationFilter,
            AppProperties appProperties
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.appProperties = appProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/resend-registration-otp",
                                "/api/auth/verify-registration-otp",
                                "/api/auth/login",
                                "/api/auth/verify-otp",
                                "/api/auth/refresh",
                                "/api/auth/logout",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()

                        .requestMatchers(
                                "/api/test/all"
                        ).permitAll()

                        .requestMatchers(
                                "/error"
                        ).permitAll()

                        .requestMatchers(
                                "/favicon.ico"
                        ).permitAll()

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/owner/**"
                        ).hasAnyRole(
                                "OWNER",
                                "ADMIN"
                        )

                        .anyRequest().authenticated()
                )

                .httpBasic(httpBasic ->
                        httpBasic.disable()
                )

                .formLogin(form ->
                        form.disable()
                )

                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        authenticationEntryPoint()
                                )
                                .accessDeniedHandler(
                                        accessDeniedHandler()
                                )
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                appProperties
                        .getCors()
                        .getAllowedOriginList()
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin"
                )
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setAllowCredentials(false);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {

        return (request, response, exception) -> {

            response.setStatus(401);

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write("""
                    {
                      "success": false,
                      "status": 401,
                      "error": "UNAUTHORIZED",
                      "message": "Authentication is required."
                    }
                    """);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {

        return (request, response, exception) -> {

            response.setStatus(403);

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write("""
                    {
                      "success": false,
                      "status": 403,
                      "error": "FORBIDDEN",
                      "message": "You do not have permission to access this resource."
                    }
                    """);
        };
    }
}