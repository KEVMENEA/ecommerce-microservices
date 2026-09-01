package com.menea.ecommerce.product_service.mapper;

import com.menea.ecommerce.product_service.api.dto.CreateProductRequest;
import com.menea.ecommerce.product_service.api.dto.ProductResponse;
import com.menea.ecommerce.product_service.domain.Product;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {

        Product product = new Product();

        product.setId(UUID.randomUUID());
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCurrency(request.currency());
        product.setActive(true);

        Instant  instant = Instant.now();
        product.setCreatedAt(instant);
        return product;
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCurrency(),
                product.isActive()
        );
    }
}
