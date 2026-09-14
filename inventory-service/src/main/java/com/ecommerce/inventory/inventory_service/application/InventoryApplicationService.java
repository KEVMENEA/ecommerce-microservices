package com.ecommerce.inventory.inventory_service.application;


import com.ecommerce.inventory.inventory_service.api.dto.*;
import com.ecommerce.inventory.inventory_service.domain.*;
import com.ecommerce.inventory.inventory_service.exception.*;
import com.ecommerce.inventory.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory.inventory_service.repository.StockMovementRepository;
import com.ecommerce.inventory.inventory_service.repository.StockReservationRepository;
import com.ecommerce.inventory.inventory_service.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private static final int RESERVATION_TTL_MINUTES = 15;

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockReservationRepository reservationRepository;
    private final StockMovementRepository movementRepository;
    private final InventoryMapper mapper;
    private final Clock clock;



    // ============================================================
    // WAREHOUSE
    // ============================================================

    @Transactional
    public WarehouseResponse createWarehouse(
            CreateWarehouseRequest request
    ) {

        if (warehouseRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException(
                    "Warehouse code already exists: "
                            + request.code()
            );
        }

        Warehouse warehouse = new Warehouse(
                request.name(),
                request.code(),
                request.address(),
                WarehouseStatus.ACTIVE
        );

        Warehouse saved =
                warehouseRepository.save(warehouse);

        return mapper.toResponse(saved);
    }



    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouse(Long warehouseId) {
        Warehouse warehouse = findWarehouse(warehouseId);
        return mapper.toResponse(warehouse);
    }

    @Transactional
    public WarehouseResponse deactivateWarehouse(
            Long warehouseId
    ) {

        Warehouse warehouse =
                findWarehouse(warehouseId);

        warehouse.deactivate();

        return mapper.toResponse(warehouse);
    }


    @Transactional
    public WarehouseResponse activateWarehouse(
            Long warehouseId
    ) {

        Warehouse warehouse =
                findWarehouse(warehouseId);

        warehouse.activate();

        return mapper.toResponse(warehouse);
    }


    // ============================================================
    // INVENTORY
    // ============================================================

    // Create Inventory
    @Transactional
    public InventoryResponse createInventory(
            CreateInventoryRequest request
    ) {

        if (inventoryRepository.existsByVariantIdAndWarehouseId(
                                request.variantId(),
                                request.warehouseId()
                        )) {

            throw new DuplicateInventoryException(
                    request.variantId(),
                    request.warehouseId()
            );
        }

        Warehouse warehouse = findWarehouse(request.warehouseId());

        Inventory inventory = new Inventory(
                request.variantId(),
                warehouse,
                request.onHandQuantity(),
                request.lowStockThreshold()
        );

        inventoryRepository.save(inventory);

        if (request.onHandQuantity() > 0) {

            saveMovement(
                    inventory,
                    null,
                    StockMovementType.STOCK_IN,
                    request.onHandQuantity(),
                    "INVENTORY_INITIALIZATION",
                    inventory.getId(),
                    "Initial inventory"
            );
        }

        return mapper.toResponse(inventory);
    }


    // Get Inventory
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(
            Long variantId,
            Long warehouseId
    ) {

        Inventory inventory = findInventory(variantId, warehouseId);

        return mapper.toResponse(inventory);
    }


    // Get all warehouse inventory for a variant
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByVariant(
            Long variantId
    ) {

        return inventoryRepository
                .findByVariantId(variantId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // Availability
    @Transactional(readOnly = true)
    public InventoryResponse getAvailability(
            Long variantId,
            Long warehouseId
    ) {

        Inventory inventory = findInventory(variantId, warehouseId);

        return mapper.toResponse(
                inventory
        );
    }


    // Low-stock inventory
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockInventory() {

        return inventoryRepository
                .findLowStockInventory()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    // ============================================================
    // RESTOCK
    // ============================================================

    @Transactional
    public InventoryResponse restock(
            Long variantId,
            Long warehouseId,
            AdjustStockRequest request
    ) {

        Inventory inventory = findInventory(variantId, warehouseId);

        inventory.addStock(request.quantity());

        saveMovement(
                inventory,
                null,
                StockMovementType.STOCK_IN,
                request.quantity(),
                "MANUAL_RESTOCK",
                null,
                request.reason()
        );

        return mapper.toResponse(inventory);
    }


    // ============================================================
    // ADJUSTMENT OUT
    // ============================================================

    @Transactional
    public InventoryResponse adjustOut(
            Long variantId,
            Long warehouseId,
            AdjustStockRequest request
    ) {

        Inventory inventory = findInventory(variantId, warehouseId);

        if (
                inventory.availableQuantity() < request.quantity()
        ) {

            throw new InsufficientStockException(
                    variantId,
                    request.quantity(),
                    inventory.availableQuantity()
            );
        }

        inventory.removeStock(request.quantity());

        saveMovement(
                inventory,
                null,
                StockMovementType.ADJUSTMENT_OUT,
                request.quantity(),
                "MANUAL_ADJUSTMENT",
                null,
                request.reason()
        );

        return mapper.toResponse(inventory);
    }


    // ============================================================
    // RESERVE
    // ============================================================

    @Transactional
    public ReservationResponse reserve(ReserveStockRequest request) {

        /*
         * Idempotency:
         *
         * If the same order and variant were already reserved,
         * return the existing reservation instead of reserving twice.
         */
        var existing = reservationRepository.findByOrderIdAndVariantId(
                                request.orderId(),
                                request.variantId()
                        );

        if (existing.isPresent()) {

            StockReservation reservation = existing.get();

            if (
                    reservation.getWarehouse().getId()
                            .equals(request.warehouseId())
                            &&
                            reservation.getQuantity()
                                    == request.quantity()
            ) {

                return mapper.toResponse(
                        reservation
                );
            }

            throw new InvalidReservationStateException(
                    "A reservation already exists for "
                            + "orderId="
                            + request.orderId()
                            + ", variantId="
                            + request.variantId()
            );
        }


        Inventory inventory = findInventory(
                        request.variantId(),
                        request.warehouseId()
                );


        if (
                inventory.availableQuantity() < request.quantity()
        ) {

            throw new InsufficientStockException(
                    request.variantId(),
                    request.quantity(),
                    inventory.availableQuantity()
            );
        }


        inventory.reserve(request.quantity());

        LocalDateTime now = LocalDateTime.now(clock);

        StockReservation reservation =
                new StockReservation(
                        UUID.randomUUID(),
                        request.orderId(),
                        request.variantId(),
                        inventory.getWarehouse(),
                        request.quantity(),
                        now.plusMinutes(
                                RESERVATION_TTL_MINUTES
                        )
                );


        reservationRepository.save(
                reservation
        );


        saveMovement(
                inventory,
                reservation,
                StockMovementType.RESERVE,
                request.quantity(),
                "ORDER",
                request.orderId(),
                "Stock reserved for order"
        );


        /*
         * inventory is managed by JPA.
         * Dirty checking persists reservedQuantity automatically.
         */

        return mapper.toResponse(
                reservation
        );
    }


    // ============================================================
    // CONFIRM
    // ============================================================

    @Transactional
    public ReservationResponse confirm(
            UUID reservationUuid
    ) {

        StockReservation reservation =
                findReservation(
                        reservationUuid
                );


        if (
                reservation.getStatus()
                        == ReservationStatus.CONFIRMED
        ) {

            /*
             * Idempotent repeated confirmation.
             */
            return mapper.toResponse(
                    reservation
            );
        }


        if (
                reservation.getStatus()
                        != ReservationStatus.RESERVED
        ) {

            throw new InvalidReservationStateException(
                    "Reservation cannot be confirmed from state "
                            + reservation.getStatus()
            );
        }


        if (
                reservation.isExpired(
                        LocalDateTime.now(clock)
                )
        ) {

            expireReservation(
                    reservation
            );

            throw new InvalidReservationStateException(
                    "Reservation has expired"
            );
        }


        Inventory inventory =
                findInventory(
                        reservation.getVariantId(),
                        reservation.getWarehouse().getId()
                );


        inventory.confirm(
                reservation.getQuantity()
        );

        reservation.confirm();


        saveMovement(
                inventory,
                reservation,
                StockMovementType.CONFIRM,
                reservation.getQuantity(),
                "ORDER",
                reservation.getOrderId(),
                "Reservation confirmed"
        );


        return mapper.toResponse(
                reservation
        );
    }


    // ============================================================
    // RELEASE
    // ============================================================

    @Transactional
    public ReservationResponse release(
            UUID reservationUuid
    ) {

        StockReservation reservation =
                findReservation(
                        reservationUuid
                );


        if (
                reservation.getStatus()
                        == ReservationStatus.RELEASED
        ) {

            return mapper.toResponse(
                    reservation
            );
        }


        if (
                reservation.getStatus()
                        != ReservationStatus.RESERVED
        ) {

            throw new InvalidReservationStateException(
                    "Reservation cannot be released from state "
                            + reservation.getStatus()
            );
        }


        Inventory inventory =
                findInventory(
                        reservation.getVariantId(),
                        reservation.getWarehouse().getId()
                );


        inventory.release(
                reservation.getQuantity()
        );

        reservation.release();


        saveMovement(
                inventory,
                reservation,
                StockMovementType.RELEASE,
                reservation.getQuantity(),
                "ORDER",
                reservation.getOrderId(),
                "Reservation released"
        );


        return mapper.toResponse(
                reservation
        );
    }


    // ============================================================
    // GET RESERVATION
    // ============================================================

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(
            UUID reservationUuid
    ) {

        return mapper.toResponse(
                findReservation(
                        reservationUuid
                )
        );
    }


    // ============================================================
    // MOVEMENT HISTORY
    // ============================================================

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    getMovementHistory(
            Long variantId,
            Long warehouseId
    ) {

        return movementRepository
                .findByVariantIdAndWarehouseIdOrderByCreatedAtDesc(
                        variantId,
                        warehouseId
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    // ============================================================
    // EXPIRATION
    // ============================================================

    @Transactional
    public int expireReservations() {

        LocalDateTime now = LocalDateTime.now(clock);


        List<StockReservation> expired =
                reservationRepository.findByStatusAndExpiresAtLessThanEqual(
                                ReservationStatus.RESERVED,
                                now);

        int count = 0;


        for (StockReservation reservation
                : expired
        ) {

            expireReservation(reservation);
            count++;
        }


        return count;
    }


    private void expireReservation(
            StockReservation reservation
    ) {

        Inventory inventory = findInventory(
                        reservation.getVariantId(),
                        reservation.getWarehouse().getId()
                );


        inventory.release(reservation.getQuantity());

        reservation.expire();


        saveMovement(
                inventory,
                reservation,
                StockMovementType.RELEASE,
                reservation.getQuantity(),
                "RESERVATION_EXPIRATION",
                reservation.getId(),
                "Reservation expired"
        );
    }


    // ============================================================
    // HELPERS
    // ============================================================

    private Inventory findInventory(
            Long variantId,
            Long warehouseId
    ) {

        return inventoryRepository
                .findByVariantIdAndWarehouseId(
                        variantId,
                        warehouseId
                )
                .orElseThrow(
                        () -> new InventoryNotFoundException(
                                        variantId,
                                        warehouseId));
    }


    private Warehouse findWarehouse(Long warehouseId) {

        return warehouseRepository
                .findById(warehouseId)
                .orElseThrow(() ->new WarehouseNotFoundException(warehouseId));
    }


    private StockReservation findReservation(UUID reservationUuid) {

        return reservationRepository
                .findByReservationUuid(
                        reservationUuid)
                .orElseThrow(() -> new ReservationNotFoundException(reservationUuid));
    }


    private void saveMovement(
            Inventory inventory,
            StockReservation reservation,
            StockMovementType type,
            int quantity,
            String referenceType,
            Long referenceId,
            String reason
    ) {

        StockMovement movement =
                new StockMovement(
                        inventory.getVariantId(),
                        inventory.getWarehouse(),
                        reservation,
                        type,
                        quantity,
                        referenceType,
                        referenceId,
                        reason
                );


        movementRepository.save(
                movement
        );
    }
}