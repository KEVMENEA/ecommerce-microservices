package com.menea.ecommerce.product_service.api;

import com.menea.ecommerce.product_service.api.dto.CreateProductRequest;
import com.menea.ecommerce.product_service.api.dto.ProductResponse;
import com.menea.ecommerce.product_service.api.dto.UpdateProductRequest;
import com.menea.ecommerce.product_service.application.ProductApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductApplicationService productService;


    // POST /api/v1/products
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @Valid @RequestBody CreateProductRequest request
    ) {
        return productService.createProduct(request);
    }


    // GET /api/v1/products/{id}
    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable UUID id
    ) {
        return productService.getProductById(id);
    }


    // GET /api/v1/products?page=0&size=10
    @GetMapping
    public Page<ProductResponse> getProducts(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {
        return productService.getProducts(pageable);
    }


    // PUT /api/v1/products/{id}
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }


    // DELETE /api/v1/products/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable UUID id
    ) {
        productService.deactivateProduct(id);
    }
}