package com.daya.app.backend.service;

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

public interface AuthenticationService {

    ApiResponse register(RegisterRequest request);

    ApiResponse verifyRegistrationOtp(VerifyOtpRequest request);

    LoginResponse login(LoginRequest request);

    JwtResponse verifyLoginOtp(VerifyOtpRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    ApiResponse logout(LogoutRequest request);

    ApiResponse forgotPassword(ForgotPasswordRequest request);

    ApiResponse resetPassword(ResetPasswordRequest request);

    ApiResponse resendRegistrationOtp(ResendOtpRequest request);
}