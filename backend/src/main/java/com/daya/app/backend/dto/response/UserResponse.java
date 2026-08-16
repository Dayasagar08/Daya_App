package com.daya.app.backend.dto.response;

import com.daya.app.backend.entity.AccountStatus;
import com.daya.app.backend.entity.ERole;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse(

        UUID uid,

        String displayName,

        String primaryEmail,

        String alternateEmail,

        String phoneNumber,

        AccountStatus accountStatus,

        Set<ERole> roles,

        LocalDateTime lastLogin

) {
}