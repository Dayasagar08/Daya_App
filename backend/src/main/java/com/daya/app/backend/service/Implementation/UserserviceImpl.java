package com.daya.app.backend.service.Implementation;

import com.daya.app.backend.dto.request.RegisterRequest;
import com.daya.app.backend.dto.response.UserResponse;
import com.daya.app.backend.entity.AccountStatus;
import com.daya.app.backend.entity.ERole;
import com.daya.app.backend.entity.Role;
import com.daya.app.backend.entity.User;
import com.daya.app.backend.exception.ApiException;
import com.daya.app.backend.exception.ErrorCode;
import com.daya.app.backend.repo.RoleRepo;
import com.daya.app.backend.repo.UserRepo;
import com.daya.app.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserserviceImpl implements UserService {

    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private static final int LOCK_DURATION_MINUTES = 15;

    private final UserRepo userRepository;

    private final RoleRepo roleRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user account.
     *
     * Public registration always assigns ROLE_USER.
     * The client cannot select an elevated role.
     */
    @Override
    public User createUser(RegisterRequest request) {

        String primaryEmail =
                normalizeEmail(request.primaryEmail());

        String alternateEmail =
                normalizeEmail(request.alternateEmail());

        /*
         * Primary email validation.
         */
        if (primaryEmail == null) {

            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Primary email is required."
            );
        }

        /*
         * Primary email must be unique.
         */
        if (userRepository.existsByPrimaryEmail(primaryEmail)) {

            throw new ApiException(
                    ErrorCode.PRIMARY_EMAIL_ALREADY_EXISTS,
                    "Primary email is already registered."
            );
        }

        /*
         * Validate alternate email.
         */
        if (alternateEmail != null) {

            /*
             * Primary and alternate email cannot be identical.
             */
            if (primaryEmail.equals(alternateEmail)) {

                throw new ApiException(
                        ErrorCode.ALTERNATE_EMAIL_ALREADY_EXISTS,
                        "Alternate email must be different from primary email."
                );
            }

            /*
             * Alternate email cannot already be another
             * user's alternate email.
             */
            if (userRepository.existsByAlternateEmail(alternateEmail)) {

                throw new ApiException(
                        ErrorCode.ALTERNATE_EMAIL_ALREADY_EXISTS,
                        "Alternate email is already registered."
                );
            }

            /*
             * Alternate email cannot already be another
             * user's primary email.
             */
            if (userRepository.existsByPrimaryEmail(alternateEmail)) {

                throw new ApiException(
                        ErrorCode.PRIMARY_EMAIL_ALREADY_EXISTS,
                        "Alternate email is already registered as a primary email."
                );
            }
        }

        /*
         * Normalize phone number.
         */
        String phoneNumber =
                normalizePhoneNumber(request.phoneNumber());

        /*
         * Phone number must be unique when supplied.
         */
        if (phoneNumber != null &&
                userRepository.existsByPhoneNumber(phoneNumber)) {

            throw new ApiException(
                    ErrorCode.PHONE_NUMBER_ALREADY_EXISTS,
                    "Phone number is already registered."
            );
        }

        /*
         * Always assign ROLE_USER during public registration.
         *
         * The requested role is intentionally NOT read from
         * RegisterRequest. This prevents users from registering
         * themselves as ADMIN, OWNER, MANAGER, etc.
         */
        Role userRole =
                roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() ->
                                new ApiException(
                                        ErrorCode.ROLE_NOT_FOUND,
                                        "Default user role was not found."
                                )
                        );

        /*
         * Create user entity.
         */
        User user = new User();

        user.setUid(UUID.randomUUID());

        user.setDisplayName(
                request.displayName().trim()
        );

        user.setPrimaryEmail(primaryEmail);

        user.setAlternateEmail(alternateEmail);

        /*
         * Password is stored as a BCrypt hash.
         */
        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setPhoneNumber(phoneNumber);

        /*
         * Neither email is verified at registration time.
         */
        user.setPrimaryEmailVerified(false);

        user.setAlternateEmailVerified(false);

        /*
         * Account becomes ACTIVE only after the required
         * verification flow is completed.
         */
        user.setAccountStatus(
                AccountStatus.PENDING_VERIFICATION
        );

        /*
 * Initialize login security fields.
 */
user.setFailedLoginAttempts(0);

user.setLockedUntil(null);

user.setLastLogin(null);

/*
 * Initialize registration OTP resend counter.
 *
 * The first OTP sent during registration is NOT counted
 * as a resend. The counter starts at zero.
 */
user.setRegistrationOtpResendCount(0);

        /*
         * Assign only the default USER role.
         */
        Set<Role> roles =
                new HashSet<>();

        roles.add(userRole);

        user.setRoles(roles);

        return userRepository.save(user);
    }

    /**
     * Finds a user using either the primary or alternate email.
     *
     * Both login identities resolve to the same User/UID.
     */
    @Override
    @Transactional(readOnly = true)
    public User findByLoginEmail(String email) {

        String normalizedEmail =
                normalizeEmail(email);

        return userRepository
                .findByLoginEmail(normalizedEmail)
                .orElseThrow(() ->
                        new ApiException(
                                ErrorCode.INVALID_CREDENTIALS,
                                "Invalid email or password."
                        )
                );
    }

    /**
     * Finds a user by their immutable UID.
     */
    @Override
    @Transactional(readOnly = true)
    public User findByUid(UUID uid) {

        return userRepository
                .findByUid(uid)
                .orElseThrow(() ->
                        new ApiException(
                                ErrorCode.USER_NOT_FOUND,
                                "User not found."
                        )
                );
    }

    /**
     * Returns the authenticated user's public profile.
     *
     * Internal entity fields such as password,
     * failed login attempts and lockedUntil are not exposed.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(UUID uid) {

        User user =
                findByUid(uid);

        return new UserResponse(
                user.getUid(),
                user.getDisplayName(),
                user.getPrimaryEmail(),
                user.getAlternateEmail(),
                user.getPhoneNumber(),
                user.getAccountStatus(),
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(
                                Collectors.toUnmodifiableSet()
                        ),
                user.getLastLogin()
        );
    }

    /**
     * Checks whether a primary email already exists.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean primaryEmailExists(String email) {

        String normalizedEmail =
                normalizeEmail(email);

        return userRepository
                .existsByPrimaryEmail(normalizedEmail);
    }

    /**
     * Checks whether an alternate email already exists.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean alternateEmailExists(String email) {

        String normalizedEmail =
                normalizeEmail(email);

        return userRepository
                .existsByAlternateEmail(normalizedEmail);
    }

    /**
     * Checks whether a phone number already exists.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean phoneNumberExists(
            String phoneNumber
    ) {

        String normalizedPhone =
                normalizePhoneNumber(phoneNumber);

        return userRepository
                .existsByPhoneNumber(normalizedPhone);
    }

    /**
     * Updates the user's last successful login time.
     */
    @Override
    public void updateLastLogin(User user) {

        user.setLastLogin(
                LocalDateTime.now()
        );

        userRepository.save(user);
    }

    /**
     * Changes the user's password.
     */
    @Override
    public void changePassword(
            User user,
            String newPassword
    ) {

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);
    }

    /**
     * Locks an account after repeated failed login attempts.
     */
    @Override
    public void lockAccount(User user) {

        user.setAccountStatus(
                AccountStatus.LOCKED
        );

        user.setLockedUntil(
                LocalDateTime.now()
                        .plusMinutes(
                                LOCK_DURATION_MINUTES
                        )
        );

        userRepository.save(user);
    }

    /**
     * Unlocks an account and resets login failure state.
     */
    @Override
    public void unlockAccount(User user) {

        user.setAccountStatus(
                AccountStatus.ACTIVE
        );

        user.setLockedUntil(null);

        user.setFailedLoginAttempts(0);

        userRepository.save(user);
    }

    /**
     * Resets failed login attempts.
     */
    @Override
    public void resetFailedLoginAttempts(
            User user
    ) {

        user.setFailedLoginAttempts(0);

        user.setLockedUntil(null);

        userRepository.save(user);
    }

    /**
     * Increments failed login attempts.
     *
     * After the configured maximum number of attempts,
     * the account is locked temporarily.
     */
    @Override
    public void incrementFailedLoginAttempts(
            User user
    ) {

        int currentAttempts =
                user.getFailedLoginAttempts() == null
                        ? 0
                        : user.getFailedLoginAttempts();

        int updatedAttempts =
                currentAttempts + 1;

        user.setFailedLoginAttempts(
                updatedAttempts
        );

        if (updatedAttempts >=
                MAX_FAILED_LOGIN_ATTEMPTS) {

            user.setAccountStatus(
                    AccountStatus.LOCKED
            );

            user.setLockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(
                                    LOCK_DURATION_MINUTES
                            )
            );
        }

        userRepository.save(user);
    }

    /**
     * Normalizes an email address consistently
     * before database operations.
     */
    private String normalizeEmail(
            String email
    ) {

        if (email == null ||
                email.isBlank()) {

            return null;
        }

        return email
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    /**
     * Normalizes a phone number.
     *
     * Currently this removes surrounding whitespace only.
     * International phone-number validation/normalization
     * can be added later if required.
     */
    private String normalizePhoneNumber(
            String phoneNumber
    ) {

        if (phoneNumber == null ||
                phoneNumber.isBlank()) {

            return null;
        }

        return phoneNumber.trim();
    }

    @Override
public void verifyPrimaryEmail(User user) {

    user.setPrimaryEmailVerified(true);

    user.setAccountStatus(
            AccountStatus.ACTIVE
    );

    userRepository.save(user);
}
}