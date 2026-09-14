package com.menea.ecommerce.user_service.application;

import com.menea.ecommerce.user_service.api.dto.RegisterUserRequest;
import com.menea.ecommerce.user_service.api.dto.UserResponse;
import com.menea.ecommerce.user_service.domain.Role;
import com.menea.ecommerce.user_service.domain.User;
import com.menea.ecommerce.user_service.domain.UserRepository;
import com.menea.ecommerce.user_service.exception.DuplicateEmailException;
import com.menea.ecommerce.user_service.exception.UserNotFoundException;
import com.menea.ecommerce.user_service.mapper.UserMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponse register(RegisterUserRequest request) {

        String normalizedEmail = request.email()
                        .trim()
                        .toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException(normalizedEmail);
        }

        User user = userMapper.toEntity(request);

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        user.setRoles(Set.of(Role.CUSTOMER));

        User saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }


    @Transactional
    public UserResponse getUserById(UUID id) {

        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id)
                );

        return userMapper.toResponse(user);
    }

    public UserResponse getByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found for Keycloak id: " + keycloakId
                        )
                );

        return userMapper.toResponse(user);
    }
}