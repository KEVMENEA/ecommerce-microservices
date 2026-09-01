package com.menea.ecommerce.user_service.mapper;

import com.menea.ecommerce.user_service.api.dto.RegisterUserRequest;
import com.menea.ecommerce.user_service.api.dto.UserResponse;
import com.menea.ecommerce.user_service.domain.User;
import com.menea.ecommerce.user_service.domain.UserStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(RegisterUserRequest request) {

        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail(request.email().toLowerCase());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setStatus(UserStatus.ACTIVE);

        Instant now = Instant.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
    }

    public UserResponse toResponse(User user) {

        Set<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus().name(),
                roles
        );
    }
}