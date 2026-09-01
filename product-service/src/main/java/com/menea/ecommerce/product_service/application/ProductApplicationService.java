package com.menea.ecommerce.product_service.application;

import com.menea.ecommerce.product_service.api.dto.CreateProductRequest;
import com.menea.ecommerce.product_service.api.dto.ProductResponse;
import com.menea.ecommerce.product_service.api.dto.UpdateProductRequest;

import com.menea.ecommerce.product_service.domain.Product;
import com.menea.ecommerce.product_service.domain.ProductRepository;

import com.menea.ecommerce.product_service.exception.DuplicateSkuException;
import com.menea.ecommerce.product_service.exception.ProductNotFoundException;
import com.menea.ecommerce.product_service.mapper.ProductMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;


    // CREATE
    @Transactional
    public ProductResponse createProduct(
            CreateProductRequest request
    ) {

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(
                    "SKU already exists: " + request.sku()
            );
        }

        Product product = mapper.toEntity(request);

        Product savedProduct =
                productRepository.save(product);

        return mapper.toResponse(savedProduct);
    }


    // GET BY ID
    @Transactional
    public ProductResponse getProductById(UUID id) {

        Product product = findActiveProduct(id);

        return mapper.toResponse(product);
    }


    // GET ALL WITH PAGINATION
    @Transactional
    public Page<ProductResponse> getProducts(
            Pageable pageable
    ) {

        return productRepository
                .findAllByActiveTrue(pageable)
                .map(mapper::toResponse);
    }


    // UPDATE
    @Transactional
    public ProductResponse updateProduct(
            UUID id,
            UpdateProductRequest request
    ) {

        Product product = findActiveProduct(id);

        product.update(
                request.name(),
                request.description(),
                request.price()
        );

        return mapper.toResponse(product);
    }


    // SOFT DELETE
    @Transactional
    public void deactivateProduct(UUID id) {

        Product product = findActiveProduct(id);

        product.deactivate();
    }


    // REUSABLE INTERNAL METHOD
    private Product findActiveProduct(UUID id) {

        return productRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(
                        () -> new ProductNotFoundException(id)
                );
    }
}