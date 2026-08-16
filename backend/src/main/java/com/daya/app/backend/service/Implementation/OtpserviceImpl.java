package com.daya.app.backend.service.Implementation;

import com.daya.app.backend.entity.LoginOtp;
import com.daya.app.backend.entity.OtpPurpose;
import com.daya.app.backend.entity.OtpStatus;
import com.daya.app.backend.entity.User;
import com.daya.app.backend.exception.ApiException;
import com.daya.app.backend.exception.ErrorCode;
import com.daya.app.backend.repo.LoginOtpRepo;
import com.daya.app.backend.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpserviceImpl implements OtpService {

    private static final int OTP_LENGTH = 6;

    private static final int OTP_EXPIRY_MINUTES = 5;

    private static final int MAX_OTP_ATTEMPTS = 5;

    private final LoginOtpRepo loginOtpRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
public GenerateOTP generateOtp(
        User user,
        String email,
        OtpPurpose purpose
) {

    invalidateOtp(user, email, purpose);

    String otp = generateSixDigitOtp();

    String otpHash = passwordEncoder.encode(otp);

    LocalDateTime now = LocalDateTime.now();

    LoginOtp loginOtp = LoginOtp.builder()
            .user(user)
            .email(email.trim().toLowerCase())
            .otpHash(otpHash)
            .purpose(purpose)
            .status(OtpStatus.PENDING)
            .otpSentAt(now)
            .expiresAt(now.plusMinutes(OTP_EXPIRY_MINUTES))
            .verifiedAt(null)
            .attempts(0)
            .build();

    LoginOtp savedOtp =
            loginOtpRepository.save(loginOtp);

    return new GenerateOTP(
            savedOtp,
            otp
    );
}

    @Override
    public void verifyOtp(
            User user,
            String email,
            String otp,
            OtpPurpose purpose
    ) {

        String normalizedEmail =
                email.trim().toLowerCase();

        LoginOtp loginOtp =
                loginOtpRepository
                        .findTopByEmailAndPurposeAndStatusOrderByCreatedAtDesc(
                                normalizedEmail,
                                purpose,
                                OtpStatus.PENDING
                        )
                        .orElseThrow(() -> new ApiException(
                                ErrorCode.INVALID_OTP,
                                "Invalid or unavailable OTP."
                        ));

        if (!loginOtp.getUser().getUid().equals(user.getUid())) {
            throw new ApiException(
                    ErrorCode.INVALID_OTP,
                    "Invalid or unavailable OTP."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(loginOtp.getExpiresAt())) {

            loginOtp.setStatus(OtpStatus.EXPIRED);

            loginOtpRepository.save(loginOtp);

            throw new ApiException(
                    ErrorCode.OTP_EXPIRED,
                    "OTP has expired."
            );
        }

        int attempts = loginOtp.getAttempts() == null
                ? 0
                : loginOtp.getAttempts();

        if (attempts >= MAX_OTP_ATTEMPTS) {

            loginOtp.setStatus(OtpStatus.CANCELLED);

            loginOtpRepository.save(loginOtp);

            throw new ApiException(
                    ErrorCode.OTP_LIMIT_EXCEEDED,
                    "Maximum OTP verification attempts exceeded."
            );
        }

        if (!passwordEncoder.matches(
                otp,
                loginOtp.getOtpHash()
        )) {

            loginOtp.setAttempts(attempts + 1);

            if (loginOtp.getAttempts() >= MAX_OTP_ATTEMPTS) {
                loginOtp.setStatus(OtpStatus.CANCELLED);
            }

            loginOtpRepository.save(loginOtp);

            throw new ApiException(
                    ErrorCode.INVALID_OTP,
                    "Invalid OTP."
            );
        }

        loginOtp.setStatus(OtpStatus.VERIFIED);
        loginOtp.setVerifiedAt(now);

        loginOtpRepository.save(loginOtp);
    }

    @Override
    public void invalidateOtp(
            User user,
            String email,
            OtpPurpose purpose
    ) {

        String normalizedEmail =
                email.trim().toLowerCase();

        loginOtpRepository
                .findTopByEmailAndPurposeAndStatusOrderByCreatedAtDesc(
                        normalizedEmail,
                        purpose,
                        OtpStatus.PENDING
                )
                .ifPresent(otp -> {
                    if (otp.getUser().getUid().equals(user.getUid())) {
                        otp.setStatus(OtpStatus.CANCELLED);
                        loginOtpRepository.save(otp);
                    }
                });
    }

    @Override
    public void invalidateAllUserOtps(User user) {

        loginOtpRepository.deleteByUser(user);
    }

    private String generateSixDigitOtp() {

    int minimum = (int) Math.pow(10, OTP_LENGTH - 1);
    int range = (int) Math.pow(10, OTP_LENGTH) - minimum;

    int otp = secureRandom.nextInt(range) + minimum;

    return String.valueOf(otp);
}

//     private record GeneratedOtpResult(
//             LoginOtp loginOtp,
//             String otp
//     ) {
//     }
}