//package com.menea.ecommerce.user_service;
//
//import com.menea.ecommerce.user_service.api.UserController;
//import com.menea.ecommerce.user_service.application.UserApplicationService;
//import com.menea.ecommerce.user_service.exception.DuplicateEmailException;
//import com.menea.ecommerce.user_service.exception.UserNotFoundException;
//
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@WebMvcTest(UserController.class)
//class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private UserApplicationService userService;
//
//
//    @Test
//    void shouldReturn404WhenUserNotFound()
//            throws Exception {
//
//        UUID id = UUID.randomUUID();
//
//        when(userService.getUserById(id))
//                .thenThrow(
//                        new UserNotFoundException(id)
//                );
//
//        mockMvc.perform(
//                        get("/api/v1/users/{id}", id)
//                )
//                .andExpect(
//                        status().isNotFound()
//                )
//                .andExpect(
//                        jsonPath("$.code")
//                                .value("USER_NOT_FOUND")
//                );
//    }
//
//
//    @Test
//    void shouldReturn409ForDuplicateEmail()
//            throws Exception {
//
//        when(userService.register(any()))
//                .thenThrow(
//                        new DuplicateEmailException(
//                                "customer@test.com"
//                        )
//                );
//
//        String body = """
//                {
//                  "email": "custome12r@test.com",
//                  "password": "Password123!",
//                  "firstName": "Test",
//                  "lastName": "Customer"
//                }
//                """;
//
//        mockMvc.perform(
//                        post("/api/v1/users/register")
//                                .contentType(
//                                        MediaType.APPLICATION_JSON
//                                )
//                                .content(body)
//                )
//                .andExpect(
//                        status().isConflict()
//                )
//                .andExpect(
//                        jsonPath("$.code")
//                                .value("DUPLICATE_EMAIL")
//                );
//    }
//}