package com.daya.app.backend.service;

import com.daya.app.backend.entity.OtpPurpose;

public interface EmailService {

    void sendOtp(
            String email,
            String displayName,
            String otp,
            OtpPurpose purpose
    );
}