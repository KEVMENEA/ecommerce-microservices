-- ============================================================
-- INVENTORY SERVICE DATABASE
-- ============================================================


-- ============================================================
-- WAREHOUSES
-- ============================================================

-- “Which warehouse/location owns the stock?”
CREATE TABLE warehouses (
                            id BIGSERIAL PRIMARY KEY,

                            name VARCHAR(150) NOT NULL,

                            code VARCHAR(50) NOT NULL,

                            address TEXT,

                            status VARCHAR(30) NOT NULL,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT uk_warehouses_code
                                UNIQUE (code)
);


-- ============================================================
-- INVENTORY
-- ============================================================

-- Stores the current stock balance for one product variant in one warehouse
-- “How many units do we have here?”

CREATE TABLE inventory (
                           id BIGSERIAL PRIMARY KEY,

    -- Logical reference to Product/Catalog service.
                           variant_id BIGINT NOT NULL,

                           warehouse_id BIGINT NOT NULL,

    -- Physical stock currently owned by warehouse.
                           on_hand_quantity INTEGER NOT NULL DEFAULT 0,

    -- Portion of on-hand stock temporarily reserved.
                           reserved_quantity INTEGER NOT NULL DEFAULT 0,

                           low_stock_threshold INTEGER NOT NULL DEFAULT 0,

                           status VARCHAR(30) NOT NULL,

    -- Hibernate optimistic locking.
                           version BIGINT NOT NULL DEFAULT 0,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           created_by BIGINT,

                           updated_at TIMESTAMP,
                           updated_by BIGINT,

                           deleted_at TIMESTAMP,
                           deleted_by BIGINT,

                           CONSTRAINT fk_inventory_warehouse
                               FOREIGN KEY (warehouse_id)
                                   REFERENCES warehouses(id),


--                         
                           CONSTRAINT uk_inventory_variant_warehouse
                               UNIQUE (variant_id, warehouse_id),

                           CONSTRAINT chk_inventory_on_hand_quantity
                               CHECK (on_hand_quantity >= 0),

                           CONSTRAINT chk_inventory_reserved_quantity
                               CHECK (reserved_quantity >= 0),

                           CONSTRAINT chk_inventory_reserved_not_above_on_hand
                               CHECK (reserved_quantity <= on_hand_quantity),

                           CONSTRAINT chk_inventory_low_stock_threshold
                               CHECK (low_stock_threshold >= 0)
);


-- ============================================================
-- STOCK RESERVATIONS
-- ============================================================

-- Temporarily holds stock for an order
-- “These units are booked for this order.”
CREATE TABLE stock_reservations (
                                    id BIGSERIAL PRIMARY KEY,

                                    reservation_uuid UUID NOT NULL,

    -- Logical reference to Order Service.
                                    order_id BIGINT NOT NULL,

    -- Logical reference to Product/Catalog Service.
                                    variant_id BIGINT NOT NULL,

                                    warehouse_id BIGINT NOT NULL,

                                    quantity INTEGER NOT NULL,

                                    status VARCHAR(30) NOT NULL,

                                    expires_at TIMESTAMP,

                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    created_by BIGINT,

                                    updated_at TIMESTAMP,
                                    updated_by BIGINT,

                                    deleted_at TIMESTAMP,
                                    deleted_by BIGINT,

                                    CONSTRAINT uk_stock_reservation_uuid
                                        UNIQUE (reservation_uuid),

                                    CONSTRAINT uk_stock_reservation_order_variant
                                        UNIQUE (order_id, variant_id),

                                    CONSTRAINT fk_stock_reservation_warehouse
                                        FOREIGN KEY (warehouse_id)
                                            REFERENCES warehouses(id),

                                    CONSTRAINT chk_stock_reservation_quantity
                                        CHECK (quantity > 0)
);


-- ============================================================
-- STOCK MOVEMENTS
-- ============================================================

-- Immutable audit/history of every stock change
-- “Why and when did stock change?”
CREATE TABLE stock_movements (
                                 id BIGSERIAL PRIMARY KEY,

    -- Logical Product/Catalog reference.
                                 variant_id BIGINT NOT NULL,

                                 warehouse_id BIGINT NOT NULL,

                                 reservation_id BIGINT,

                                 movement_type VARCHAR(40) NOT NULL,

                                 quantity INTEGER NOT NULL,

                                 reference_type VARCHAR(50),
                                 reference_id BIGINT,

                                 reason VARCHAR(255),

                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 created_by BIGINT,

                                 CONSTRAINT fk_stock_movement_warehouse
                                     FOREIGN KEY (warehouse_id)
                                         REFERENCES warehouses(id),

                                 CONSTRAINT fk_stock_movement_reservation
                                     FOREIGN KEY (reservation_id)
                                         REFERENCES stock_reservations(id),

                                 CONSTRAINT chk_stock_movement_quantity
                                     CHECK (quantity > 0)
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_inventory_variant_id
    ON inventory(variant_id);

CREATE INDEX idx_inventory_warehouse_id
    ON inventory(warehouse_id);


CREATE INDEX idx_stock_reservations_order_id
    ON stock_reservations(order_id);

CREATE INDEX idx_stock_reservations_variant_id
    ON stock_reservations(variant_id);

CREATE INDEX idx_stock_reservations_warehouse_id
    ON stock_reservations(warehouse_id);

CREATE INDEX idx_stock_reservations_status
    ON stock_reservations(status);

CREATE INDEX idx_stock_reservations_expires_at
    ON stock_reservations(expires_at);


CREATE INDEX idx_stock_movements_variant_id
    ON stock_movements(variant_id);

CREATE INDEX idx_stock_movements_warehouse_id
    ON stock_movements(warehouse_id);

CREATE INDEX idx_stock_movements_reservation_id
    ON stock_movements(reservation_id);

CREATE INDEX idx_stock_movements_created_at
    ON stock_movements(created_at);