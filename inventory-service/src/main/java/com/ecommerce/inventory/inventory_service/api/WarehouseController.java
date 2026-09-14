package com.ecommerce.inventory.inventory_service.api;


import com.ecommerce.inventory.inventory_service.api.dto.CreateWarehouseRequest;
import com.ecommerce.inventory.inventory_service.api.dto.WarehouseResponse;
import com.ecommerce.inventory.inventory_service.application.InventoryApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final InventoryApplicationService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponse createWarehouse(
            @Valid @RequestBody CreateWarehouseRequest request
    ) {
        return service.createWarehouse(request);
    }

    @GetMapping("/{warehouseId}")
    public WarehouseResponse getWarehouse(
            @PathVariable
            Long warehouseId
    ) {

        return service.getWarehouse(warehouseId);
    }


    @PostMapping("/{warehouseId}/activate")
    public WarehouseResponse activateWarehouse(
            @PathVariable
            Long warehouseId
    ) {

        return service.activateWarehouse(warehouseId);
    }


    @PostMapping("/{warehouseId}/deactivate")
    public WarehouseResponse deactivateWarehouse(
            @PathVariable
            Long warehouseId
    ) {

        return service.deactivateWarehouse(warehouseId);
    }
}