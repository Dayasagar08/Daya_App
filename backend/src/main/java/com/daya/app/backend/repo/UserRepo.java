package com.daya.app.backend.repo;

import com.daya.app.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUid(UUID uid);

    Optional<User> findByPrimaryEmail(String primaryEmail);

    Optional<User> findByAlternateEmail(String alternateEmail);

    Optional<User> findByPrimaryEmailOrAlternateEmail(
        String primaryEmail, String alternateEmail);

    boolean existsByPrimaryEmail(String primaryEmail);

    boolean existsByAlternateEmail(String alternateEmail);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUid(UUID uid);
    
    default Optional<User> findByLoginEmail(String email) {
    return findByPrimaryEmailOrAlternateEmail(email, email);
}
}