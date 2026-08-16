package com.daya.app.backend.control;

import com.daya.app.backend.dto.response.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test")
public class TestControl {

    @GetMapping("/authenticated")
    public ApiResponse authenticated(
            Authentication authentication
    ) {

        return new ApiResponse(
                true,
                "JWT authentication is working. User: "
                        + authentication.getName(),
                LocalDateTime.now()
        );
    }
}