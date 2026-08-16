package com.daya.app.backend.control;

import com.daya.app.backend.dto.response.UserResponse;
import com.daya.app.backend.security.CustomUserDetails;
import com.daya.app.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserControl {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        UUID uid = UUID.fromString(
                userDetails.getUid()
        );

        UserResponse response =
                userService.getUserProfile(uid);

        return ResponseEntity.ok(response);
    }
}