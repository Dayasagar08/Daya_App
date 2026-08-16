package com.daya.app.backend.service;

import com.daya.app.backend.entity.OtpPurpose;
import com.daya.app.backend.entity.User;
import com.daya.app.backend.service.Implementation.GenerateOTP;

public interface OtpService {

    GenerateOTP generateOtp(
            User user,
            String email,
            OtpPurpose purpose
    );

    void verifyOtp(
            User user,
            String email,
            String otp,
            OtpPurpose purpose
    );

    void invalidateOtp(
            User user,
            String email,
            OtpPurpose purpose
    );

    void invalidateAllUserOtps(User user);
}