package com.daya.app.backend.service;

import com.daya.app.backend.entity.RefreshToken;
import com.daya.app.backend.entity.User;

public interface RefreshTokenService {

    String create(User user);

    RefreshToken verify(String rawToken);

    String rotate(String rawToken);

    void revoke(String rawToken);

    void revokeAll(User user);
}