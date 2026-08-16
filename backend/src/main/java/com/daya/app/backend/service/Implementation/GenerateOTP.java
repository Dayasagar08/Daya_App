package com.daya.app.backend.service.Implementation;

import com.daya.app.backend.entity.LoginOtp;

public record GenerateOTP(LoginOtp loginOtp,
        String rawOtp) {
    
}