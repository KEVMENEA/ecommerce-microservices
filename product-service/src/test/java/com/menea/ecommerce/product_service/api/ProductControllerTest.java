package com.menea.ecommerce.product_service.api;


import com.menea.ecommerce.product_service.application.ProductApplicationService;
import com.menea.ecommerce.product_service.exception.ProductNotFoundException;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.when;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductApplicationService productService;


    @Test
    void shouldReturn404WhenProductNotFound()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(productService.getProductById(id))
                .thenThrow(
                        new ProductNotFoundException(id)
                );

        mockMvc.perform(
                        get("/api/v1/products/{id}", id)
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.code")
                                .value("PRODUCT_NOT_FOUND")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );
    }


}