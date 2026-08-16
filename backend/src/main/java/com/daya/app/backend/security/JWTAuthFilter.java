package com.daya.app.backend.security;

import com.daya.app.backend.entity.User;
import com.daya.app.backend.service.token.JwtService;
import com.daya.app.backend.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER =
            "Authorization";

    private static final String BEARER_PREFIX =
            "Bearer ";

    private final JwtService jwtService;

    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader(AUTHORIZATION_HEADER);

        /*
         * No Authorization header.
         *
         * Continue the chain because the endpoint may be public.
         */
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith(BEARER_PREFIX)) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(
                        BEARER_PREFIX.length()
                ).trim();

        /*
         * Empty Bearer token.
         */
        if (token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            /*
             * Validate signature and standard JWT claims.
             */
            if (!jwtService.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            /*
             * Do not replace an authentication that may already
             * have been established by another security mechanism.
             */
            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                UUID uid =
                        jwtService.extractUid(token);

                User user =
                        userService.findByUid(uid);

                /*
                 * Account must be active before the JWT is accepted.
                 */
                CustomUserDetails userDetails =
                        new CustomUserDetails(user);

                if (!userDetails.isEnabled()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (JwtException | IllegalArgumentException exception) {

    SecurityContextHolder.clearContext();
            /*
             * Invalid/expired JWT.
             * Don't expose JWT implementation details to the client.
             * Spring Security will reject protected resources if
             * authentication is required.
             * User no longer exists or another authentication-related
             * problem occurred.
             */
        filterChain.doFilter(request, response);
    }
    }
}