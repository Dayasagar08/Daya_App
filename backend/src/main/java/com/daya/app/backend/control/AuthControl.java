package com.daya.app.backend.control;

import com.daya.app.backend.dto.request.ForgotPasswordRequest;
import com.daya.app.backend.dto.request.LoginRequest;
import com.daya.app.backend.dto.request.LogoutRequest;
import com.daya.app.backend.dto.request.RefreshTokenRequest;
import com.daya.app.backend.dto.request.RegisterRequest;
import com.daya.app.backend.dto.request.ResendOtpRequest;
import com.daya.app.backend.dto.request.ResetPasswordRequest;
import com.daya.app.backend.dto.request.VerifyOtpRequest;
import com.daya.app.backend.dto.response.ApiResponse;
import com.daya.app.backend.dto.response.JwtResponse;
import com.daya.app.backend.dto.response.LoginResponse;
import com.daya.app.backend.dto.response.RefreshTokenResponse;
import com.daya.app.backend.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthControl {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        ApiResponse response =
                authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify-registration-otp")
public ResponseEntity<ApiResponse> verifyRegistrationOtp(
        @Valid @RequestBody VerifyOtpRequest request
) {

    ApiResponse response =
            authenticationService.verifyRegistrationOtp(request);

    return ResponseEntity.ok(response);
}

    @PostMapping("/verify-otp")
    public ResponseEntity<JwtResponse> verifyLoginOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {

        JwtResponse response =
                authenticationService.verifyLoginOtp(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        

        ApiResponse response =
                authenticationService.logout(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        
        RefreshTokenResponse response =
                authenticationService.refreshToken(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        ApiResponse response =
                authenticationService.forgotPassword(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        ApiResponse response =
                authenticationService.resetPassword(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-registration-otp")
public ResponseEntity<ApiResponse> resendRegistrationOtp(
        @Valid @RequestBody ResendOtpRequest request
) {

    ApiResponse response =
            authenticationService.resendRegistrationOtp(request);

    return ResponseEntity.ok(response);
}
}