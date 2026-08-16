package com.daya.app.backend.service;

import com.daya.app.backend.dto.request.RegisterRequest;
import com.daya.app.backend.dto.response.UserResponse;
import com.daya.app.backend.entity.User;

import java.util.UUID;

public interface UserService {

    User createUser(RegisterRequest request);

    User findByLoginEmail(String email);

    User findByUid(UUID uid);

    UserResponse getUserProfile(UUID uid);

    boolean primaryEmailExists(String email);

    boolean alternateEmailExists(String email);

    boolean phoneNumberExists(String phoneNumber);

    void updateLastLogin(User user);

    void changePassword(User user, String newPassword);

    void lockAccount(User user);

    void unlockAccount(User user);

    void resetFailedLoginAttempts(User user);

    void incrementFailedLoginAttempts(User user);

    void verifyPrimaryEmail(User user);
}