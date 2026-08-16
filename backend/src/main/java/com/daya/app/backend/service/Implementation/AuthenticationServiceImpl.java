package com.daya.app.backend.service.Implementation;

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
import com.daya.app.backend.entity.AccountStatus;
import com.daya.app.backend.entity.OtpPurpose;
import com.daya.app.backend.entity.User;
import com.daya.app.backend.entity.RefreshToken;
import com.daya.app.backend.exception.ApiException;
import com.daya.app.backend.exception.ErrorCode;
import com.daya.app.backend.service.token.JwtService;
import com.daya.app.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;

    private final OtpService otpService;

    private final EmailService emailService;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    private static final int MAX_REGISTRATION_OTP_RESENDS = 3;

    @Override
    public ApiResponse register(RegisterRequest request) {

        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException(
                    ErrorCode.INVALID_PASSWORD,
                    "Password and confirm password do not match."
            );
        }

        User user = userService.createUser(request);

        GenerateOTP generatedOtp =
                otpService.generateOtp(
                        user,
                        user.getPrimaryEmail(),
                        OtpPurpose.REGISTER
                );

        emailService.sendOtp(
                user.getPrimaryEmail(),
                user.getDisplayName(),
                generatedOtp.rawOtp(),
                OtpPurpose.REGISTER
        );

        return new ApiResponse(
                true,
                "Registration successful. Please verify your email.",
                LocalDateTime.now()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user =
                userService.findByLoginEmail(request.email());

        validateAccountStatus(user);

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {

            userService.incrementFailedLoginAttempts(user);

            throw new ApiException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid email or password."
            );
        }

        userService.resetFailedLoginAttempts(user);

        /*
         * Login OTP is sent to the exact email address
         * used for this login attempt.
         */
        String loginEmail =
                request.email().trim().toLowerCase();

        GenerateOTP generatedOtp =
                otpService.generateOtp(
                        user,
                        loginEmail,
                        OtpPurpose.LOGIN
                );

        emailService.sendOtp(
                loginEmail,
                user.getDisplayName(),
                generatedOtp.rawOtp(),
                OtpPurpose.LOGIN
        );

        return new LoginResponse(
                true,
                "Verification code sent to your email.",
                generatedOtp.loginOtp().getExpiresAt()
        );
    }
    @Override
public ApiResponse verifyRegistrationOtp(
        VerifyOtpRequest request
) {

    User user =
            userService.findByLoginEmail(request.email());

    /*
     * Registration verification is only allowed
     * while the account is pending verification.
     */
    if (user.getAccountStatus()
            != AccountStatus.PENDING_VERIFICATION) {

        if (Boolean.TRUE.equals(
                user.getPrimaryEmailVerified()
        )) {

            throw new ApiException(
                    ErrorCode.EMAIL_ALREADY_VERIFIED,
                    "Email address has already been verified."
            );
        }

        throw new ApiException(
                ErrorCode.INVALID_OTP,
                "Registration verification is not available for this account."
        );
    }

    String email =
            request.email()
                    .trim()
                    .toLowerCase();

    /*
     * Verify REGISTER OTP.
     */
    otpService.verifyOtp(
            user,
            email,
            request.otp(),
            OtpPurpose.REGISTER
    );

    /*
     * OTP verification succeeded.
     */
    userService.verifyPrimaryEmail(user);

    return new ApiResponse(
            true,
            "Email verified successfully. Your account is now active.",
            LocalDateTime.now()
    );
}

    @Override
    public JwtResponse verifyLoginOtp(
            VerifyOtpRequest request
    ) {

        User user =
                userService.findByLoginEmail(request.email());

        validateAccountStatus(user);

        String loginEmail =
                request.email().trim().toLowerCase();

        otpService.verifyOtp(
                user,
                loginEmail,
                request.otp(),
                OtpPurpose.LOGIN
        );

        /*
         * OTP verification succeeded.
         * Only now do we issue JWTs.
         */
        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                refreshTokenService.create(user);

        userService.updateLastLogin(user);

        return new JwtResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    @Override
    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request
    ) {

        RefreshToken currentToken =
                refreshTokenService.verify(
                        request.refreshToken()
                );

        User user = currentToken.getUser();

        validateAccountStatus(user);

        /*
         * Rotate the refresh token.
         */
        String newRefreshToken =
                refreshTokenService.rotate(
                        request.refreshToken()
                );

        String newAccessToken =
                jwtService.generateAccessToken(user);

        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    @Override
    public ApiResponse logout(
            LogoutRequest request
    ) {

        refreshTokenService.revoke(
                request.refreshToken()
        );

        return new ApiResponse(
                true,
                "Logged out successfully.",
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse forgotPassword(
            ForgotPasswordRequest request
    ) {

        User user =
                userService.findByLoginEmail(request.email());

        validateAccountStatusForPasswordReset(user);

        String email =
                request.email().trim().toLowerCase();

        GenerateOTP generatedOtp =
                otpService.generateOtp(
                        user,
                        email,
                        OtpPurpose.RESET_PASSWORD
                );

        emailService.sendOtp(
                email,
                user.getDisplayName(),
                generatedOtp.rawOtp(),
                OtpPurpose.RESET_PASSWORD
        );

        return new ApiResponse(
                true,
                "If the account exists, a password reset code has been sent.",
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse resetPassword(
            ResetPasswordRequest request
    ) {

        User user =
                userService.findByLoginEmail(request.email());

        String email =
                request.email().trim().toLowerCase();

        otpService.verifyOtp(
                user,
                email,
                request.otp(),
                OtpPurpose.RESET_PASSWORD
        );

        userService.changePassword(
                user,
                request.newPassword()
        );

        /*
         * Password changed successfully.
         * Revoke all existing sessions so previously issued
         * refresh tokens cannot continue to authenticate.
         */
        refreshTokenService.revokeAll(user);

        return new ApiResponse(
                true,
                "Password reset successfully.",
                LocalDateTime.now()
        );
    }

    private void validateAccountStatus(User user) {

        AccountStatus status =
                user.getAccountStatus();

        if (status == null) {
            throw new ApiException(
                    ErrorCode.ACCOUNT_DISABLED,
                    "Account status is invalid."
            );
        }

        switch (status) {

            case ACTIVE -> {
                // Continue authentication.
            }

            case PENDING_VERIFICATION ->
                    throw new ApiException(
                            ErrorCode.EMAIL_NOT_VERIFIED,
                            "Email address has not been verified."
                    );

            case LOCKED -> {

                if (user.getLockedUntil() != null &&
                        LocalDateTime.now()
                                .isAfter(user.getLockedUntil())) {

                    userService.unlockAccount(user);

                    return;
                }

                throw new ApiException(
                        ErrorCode.ACCOUNT_LOCKED,
                        "Account is temporarily locked."
                );
            }

            case DISABLED ->
                    throw new ApiException(
                            ErrorCode.ACCOUNT_DISABLED,
                            "Account has been disabled."
                    );

            case SUSPENDED ->
                    throw new ApiException(
                            ErrorCode.ACCOUNT_SUSPENDED,
                            "Account has been suspended."
                    );

            case DELETED ->
                    throw new ApiException(
                            ErrorCode.USER_NOT_FOUND,
                            "Account does not exist."
                    );
        }
    }

    private void validateAccountStatusForPasswordReset(
            User user
    ) {

        AccountStatus status =
                user.getAccountStatus();

        if (status == AccountStatus.DELETED ||
                status == AccountStatus.DISABLED ||
                status == AccountStatus.SUSPENDED) {

            throw new ApiException(
                    ErrorCode.USER_NOT_FOUND,
                    "Account does not exist."
            );
        }
    }
    
    @Override
public ApiResponse resendRegistrationOtp(
        ResendOtpRequest request
) {

    User user =
            userService.findByLoginEmail(request.email());

    if (user.getAccountStatus()
            != AccountStatus.PENDING_VERIFICATION) {

        if (Boolean.TRUE.equals(
                user.getPrimaryEmailVerified()
        )) {

            throw new ApiException(
                    ErrorCode.EMAIL_ALREADY_VERIFIED,
                    "Email address has already been verified."
            );
        }

        throw new ApiException(
                ErrorCode.INVALID_OTP,
                "Registration verification is not available for this account."
        );
    }

    int resendCount =
            user.getRegistrationOtpResendCount() == null
                    ? 0
                    : user.getRegistrationOtpResendCount();

    if (resendCount >= MAX_REGISTRATION_OTP_RESENDS) {

        throw new ApiException(
                ErrorCode.OTP_LIMIT_EXCEEDED,
                "Maximum registration OTP resend limit exceeded. Please contact support."
        );
    }

    String email =
            request.email()
                    .trim()
                    .toLowerCase();

    GenerateOTP generatedOtp =
            otpService.generateOtp(
                    user,
                    email,
                    OtpPurpose.REGISTER
            );

    emailService.sendOtp(
            email,
            user.getDisplayName(),
            generatedOtp.rawOtp(),
            OtpPurpose.REGISTER
    );

    user.setRegistrationOtpResendCount(
            resendCount + 1
    );

    return new ApiResponse(
            true,
            "A new verification code has been sent to your email.",
            LocalDateTime.now()
    );
}
}