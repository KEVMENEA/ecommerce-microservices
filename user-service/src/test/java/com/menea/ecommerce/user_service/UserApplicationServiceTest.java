package com.menea.ecommerce.user_service;

import com.menea.ecommerce.user_service.api.dto.RegisterUserRequest;
import com.menea.ecommerce.user_service.api.dto.UserResponse;
import com.menea.ecommerce.user_service.application.UserApplicationService;
import com.menea.ecommerce.user_service.domain.Role;
import com.menea.ecommerce.user_service.domain.User;
import com.menea.ecommerce.user_service.domain.UserRepository;
import com.menea.ecommerce.user_service.exception.DuplicateEmailException;
import com.menea.ecommerce.user_service.mapper.UserMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserApplicationService userService;


    @Test
    void shouldHashPasswordAndAssignCustomerRole() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "customer@test.com",
                        "Password123!",
                        "Test",
                        "Customer"
                );

        User user = new User();

        User savedUser = user;

        UserResponse response =
                mock(UserResponse.class);

        when(userRepository.existsByEmail(
                "customer@test.com"
        )).thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode(
                "Password123!"
        )).thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(userMapper.toResponse(savedUser))
                .thenReturn(response);

        UserResponse result =
                userService.register(request);

        assertEquals(response, result);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(captor.capture());

        User capturedUser =
                captor.getValue();

        assertEquals(
                "hashed-password",
                capturedUser.getPasswordHash()
        );

        assertEquals(
                Set.of(Role.CUSTOMER),
                capturedUser.getRoles()
        );
    }

    @Test
    void shouldRejectDuplicateEmail() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "customer@test.com",
                        "Password123!",
                        "Test",
                        "Customer"
                );

        when(userRepository.existsByEmail(
                "customer@test.com"
        )).thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.register(request)
        );

        verify(userRepository, never())
                .save(any());
    }
}