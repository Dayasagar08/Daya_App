package com.daya.app.backend.repo;

import com.daya.app.backend.entity.RefreshToken;
import com.daya.app.backend.entity.TokenStatus;
import com.daya.app.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenId(UUID tokenId);

    List<RefreshToken> findByUserAndStatus(
            User user,
            TokenStatus status
    );

}
