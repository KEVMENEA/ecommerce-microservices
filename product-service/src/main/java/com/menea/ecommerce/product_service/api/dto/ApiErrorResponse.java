package com.menea.ecommerce.product_service.api.dto;

import java.time.Instant;

public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String code,
    String message,
    String path)
{}
