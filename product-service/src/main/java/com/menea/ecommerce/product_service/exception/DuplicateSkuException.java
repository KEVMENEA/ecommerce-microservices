package com.menea.ecommerce.product_service.exception;

public class DuplicateSkuException extends RuntimeException{

    public DuplicateSkuException(String sku)
    {
        super("SKU not found: " + sku);
    }
}
