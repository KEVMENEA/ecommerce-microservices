package com.ecommerce.inventory.inventory_service.exception;


import com.ecommerce.inventory.inventory_service.api.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {


    @ExceptionHandler(
            InventoryNotFoundException.class
    )
    public ResponseEntity<ApiError>
    handleInventoryNotFound(
            InventoryNotFoundException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.NOT_FOUND,
                "INVENTORY_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(
            ReservationNotFoundException.class
    )
    public ResponseEntity<ApiError>
    handleReservationNotFound(
            ReservationNotFoundException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.NOT_FOUND,
                "RESERVATION_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(
            WarehouseNotFoundException.class
    )
    public ResponseEntity<ApiError>
    handleWarehouseNotFound(
            WarehouseNotFoundException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.NOT_FOUND,
                "WAREHOUSE_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(
            InsufficientStockException.class
    )
    public ResponseEntity<ApiError>
    handleInsufficientStock(
            InsufficientStockException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.CONFLICT,
                "INSUFFICIENT_STOCK",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(
            DuplicateInventoryException.class
    )
    public ResponseEntity<ApiError>
    handleDuplicateInventory(
            DuplicateInventoryException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.CONFLICT,
                "INVENTORY_ALREADY_EXISTS",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(
            InvalidReservationStateException.class
    )
    public ResponseEntity<ApiError>
    handleInvalidReservationState(
            InvalidReservationStateException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.CONFLICT,
                "INVALID_RESERVATION_STATE",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(
            OptimisticLockingFailureException.class
    )
    public ResponseEntity<ApiError>
    handleOptimisticLock(
            OptimisticLockingFailureException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.CONFLICT,
                "INVENTORY_CONCURRENT_UPDATE",
                "Inventory changed concurrently. Retry the request.",
                request
        );
    }


    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiError>
    handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(
                                error ->
                                        error.getField()
                                                + ": "
                                                + error.getDefaultMessage()
                        )
                        .orElse(
                                "Invalid request"
                        );


        return error(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                message,
                request
        );
    }


    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ApiError>
    handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                ex.getMessage(),
                request
        );
    }


    private ResponseEntity<ApiError> error(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {

        ApiError response =
                new ApiError(
                        Instant.now(),
                        status.value(),
                        code,
                        message,
                        request.getRequestURI()
                );


        return ResponseEntity
                .status(status)
                .body(response);
    }
}
