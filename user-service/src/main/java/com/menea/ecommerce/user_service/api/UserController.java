package com.menea.ecommerce.user_service.api;

import com.menea.ecommerce.user_service.api.dto.RegisterUserRequest;
import com.menea.ecommerce.user_service.api.dto.UserResponse;
import com.menea.ecommerce.user_service.application.UserApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserApplicationService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        return userService.register(registerUserRequest);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable UUID id
    ) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return userService.getByKeycloakId(jwt.getSubject());
    }
}


