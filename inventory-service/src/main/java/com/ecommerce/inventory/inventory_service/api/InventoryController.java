package com.ecommerce.inventory.inventory_service.api;

import com.ecommerce.inventory.inventory_service.api.dto.AdjustStockRequest;
import com.ecommerce.inventory.inventory_service.api.dto.CreateInventoryRequest;
import com.ecommerce.inventory.inventory_service.api.dto.InventoryResponse;
import com.ecommerce.inventory.inventory_service.api.dto.StockMovementResponse;
import com.ecommerce.inventory.inventory_service.application.InventoryApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryApplicationService service;


    // ============================================================
    // CREATE
    // ============================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createInventory(
            @Valid
            @RequestBody
            CreateInventoryRequest request
    ) {

        return service.createInventory(
                request
        );
    }


    // ============================================================
    // GET ONE
    // ============================================================

    @GetMapping("/{variantId}/warehouses/{warehouseId}")
    public InventoryResponse getInventory(@PathVariable Long variantId,
                                          @PathVariable Long warehouseId
    ) {

        return service.getInventory(
                variantId,
                warehouseId
        );
    }


    // ============================================================
    // GET BY VARIANT
    // ============================================================

    @GetMapping("/{variantId}")
    public List<InventoryResponse>
    getInventoryByVariant(@PathVariable Long variantId
    ) {

        return service.getInventoryByVariant(
                variantId
        );
    }


    // ============================================================
    // AVAILABILITY
    // ============================================================

    @GetMapping("/{variantId}/warehouses/{warehouseId}/availability")
    public InventoryResponse getAvailability(

            @PathVariable
            Long variantId,

            @PathVariable
            Long warehouseId
    ) {

        return service.getAvailability(
                variantId,
                warehouseId
        );
    }


    // ============================================================
    // LOW STOCK
    // ============================================================

    @GetMapping("/low-stock")
    public List<InventoryResponse>
    getLowStockInventory() {

        return service
                .getLowStockInventory();
    }


    // ============================================================
    // RESTOCK
    // ============================================================

    @PostMapping("/{variantId}/warehouses/{warehouseId}/restock")
    public InventoryResponse restock(

            @PathVariable
            Long variantId,

            @PathVariable
            Long warehouseId,

            @Valid
            @RequestBody
            AdjustStockRequest request
    ) {

        return service.restock(
                variantId,
                warehouseId,
                request
        );
    }


    // ============================================================
    // ADJUST OUT
    // ============================================================

    @PostMapping("/{variantId}/warehouses/{warehouseId}/adjust-out")
    public InventoryResponse adjustOut(

            @PathVariable
            Long variantId,

            @PathVariable
            Long warehouseId,

            @Valid
            @RequestBody
            AdjustStockRequest request
    ) {

        return service.adjustOut(
                variantId,
                warehouseId,
                request
        );
    }


    // ============================================================
    // MOVEMENT HISTORY
    // ============================================================

    @GetMapping("/{variantId}/warehouses/{warehouseId}/movements")
    public List<StockMovementResponse> getMovementHistory(@PathVariable Long variantId, @PathVariable Long warehouseId
    ) {

        return service.getMovementHistory(
                variantId,
                warehouseId
        );
    }

}
