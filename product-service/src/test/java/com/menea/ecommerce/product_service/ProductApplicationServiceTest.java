package com.menea.ecommerce.product_service;

import com.menea.ecommerce.product_service.api.dto.ProductResponse;
import com.menea.ecommerce.product_service.application.ProductApplicationService;
import com.menea.ecommerce.product_service.domain.Product;
import com.menea.ecommerce.product_service.domain.ProductRepository;
import com.menea.ecommerce.product_service.exception.ProductNotFoundException;
import com.menea.ecommerce.product_service.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductApplicationService productService;

    @Test
    void shouldGetActiveProductById() {

        UUID id = UUID.randomUUID();

        Product product = new Product();

        ProductResponse response = mock(ProductResponse.class);

        when(productRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.of(product));

        when(mapper.toResponse(product))
                .thenReturn(response);

        ProductResponse result =
                productService.getProductById(id);

        assertEquals(response, result);

        verify(productRepository)
                .findByIdAndActiveTrue(id);

        verify(mapper)
                .toResponse(product);
    }

    @Test
    void shouldThrowWhenProductNotFound() {

        UUID id = UUID.randomUUID();

        when(productRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(id)
        );

        verify(mapper, never())
                .toResponse(any());
    }
    @Test
    void shouldReturnPaginatedProducts() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();

        Page<Product> page = new PageImpl<>(List.of(product));

        ProductResponse response = mock(ProductResponse.class);

        when(productRepository.findAllByActiveTrue(pageable))
                .thenReturn(page);

        when(mapper.toResponse(product))
                .thenReturn(response);

        Page<ProductResponse> result =
                productService.getProducts(pageable);

        assertEquals(1, result.getTotalElements());

        assertEquals(response, result.getContent().getFirst());
    }

    }
