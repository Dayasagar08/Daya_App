package com.daya.app.backend.repo;

import com.daya.app.backend.entity.LoginOtp;
import com.daya.app.backend.entity.OtpPurpose;
import com.daya.app.backend.entity.OtpStatus;
import com.daya.app.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoginOtpRepo extends JpaRepository<LoginOtp, UUID> {

    Optional<LoginOtp> findTopByUserAndPurposeAndStatusOrderByCreatedAtDesc(
            User user,
            OtpPurpose purpose,
            OtpStatus status
    );

    Optional<LoginOtp> findTopByEmailAndPurposeAndStatusOrderByCreatedAtDesc(
            String email,
            OtpPurpose purpose,
            OtpStatus status
    );

    void deleteByUser(User user);

}