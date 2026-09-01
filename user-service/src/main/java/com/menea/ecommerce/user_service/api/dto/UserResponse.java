package com.menea.ecommerce.user_service.api.dto;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String status,
        Set<String> roles
) {}