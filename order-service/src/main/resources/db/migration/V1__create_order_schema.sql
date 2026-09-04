CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,

                        order_uuid UUID NOT NULL,
                        order_number VARCHAR(50) NOT NULL,

                        user_id BIGINT NOT NULL,

                        status VARCHAR(30) NOT NULL,
                        payment_status VARCHAR(30) NOT NULL,
                        shipping_status VARCHAR(30) NOT NULL,

                        subtotal DECIMAL(19,2) NOT NULL,

                        discount_amount DECIMAL(19,2)
                            NOT NULL DEFAULT 0,

                        shipping_fee DECIMAL(19,2)
                            NOT NULL DEFAULT 0,

                        final_amount DECIMAL(19,2) NOT NULL,

                        coupon_code VARCHAR(50),

                        shipping_address_snapshot JSONB NOT NULL,

                        idempotency_key VARCHAR(120),

                        placed_at TIMESTAMP,
                        paid_at TIMESTAMP,
                        cancelled_at TIMESTAMP,

                        created_at TIMESTAMP NOT NULL,
                        created_by BIGINT,

                        updated_at TIMESTAMP,
                        updated_by BIGINT,

                        deleted_at TIMESTAMP,
                        deleted_by BIGINT,

                        version BIGINT NOT NULL DEFAULT 0,

                        CONSTRAINT uk_orders_order_uuid
                            UNIQUE (order_uuid),

                        CONSTRAINT uk_orders_order_number
                            UNIQUE (order_number),

                        CONSTRAINT uk_orders_user_idempotency_key
                            UNIQUE (user_id, idempotency_key),

                        CONSTRAINT chk_orders_subtotal
                            CHECK (subtotal >= 0),

                        CONSTRAINT chk_orders_discount_amount
                            CHECK (discount_amount >= 0),

                        CONSTRAINT chk_orders_shipping_fee
                            CHECK (shipping_fee >= 0),

                        CONSTRAINT chk_orders_final_amount
                            CHECK (final_amount >= 0)
);


CREATE INDEX idx_orders_user_id
    ON orders(user_id);

CREATE INDEX idx_orders_status
    ON orders(status);

CREATE INDEX idx_orders_created_at
    ON orders(created_at);



CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,

                             order_id BIGINT NOT NULL,

                             product_id BIGINT NOT NULL,
                             variant_id BIGINT NOT NULL,

                             product_name_snapshot VARCHAR(255)
                                 NOT NULL,

                             sku_snapshot VARCHAR(100)
                                 NOT NULL,

                             price_snapshot DECIMAL(19,2)
                                 NOT NULL,

                             quantity INT NOT NULL,

                             subtotal DECIMAL(19,2)
                                 NOT NULL,

                             image_snapshot TEXT,

                             created_at TIMESTAMP NOT NULL,
                             created_by BIGINT,

                             updated_at TIMESTAMP,
                             updated_by BIGINT,

                             deleted_at TIMESTAMP,
                             deleted_by BIGINT,

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id),

                             CONSTRAINT chk_order_items_quantity
                                 CHECK (quantity > 0),

                             CONSTRAINT chk_order_items_price
                                 CHECK (price_snapshot >= 0),

                             CONSTRAINT chk_order_items_subtotal
                                 CHECK (subtotal >= 0)
);


CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);

CREATE INDEX idx_order_items_product_id
    ON order_items(product_id);

CREATE INDEX idx_order_items_variant_id
    ON order_items(variant_id);



CREATE TABLE order_status_history (
                                      id BIGSERIAL PRIMARY KEY,

                                      order_id BIGINT NOT NULL,

                                      old_status VARCHAR(30),

                                      new_status VARCHAR(30)
                                                      NOT NULL,

                                      reason VARCHAR(255),

                                      created_at TIMESTAMP NOT NULL,
                                      created_by BIGINT,

                                      CONSTRAINT fk_order_status_history_order
                                          FOREIGN KEY (order_id)
                                              REFERENCES orders(id)
);


CREATE INDEX idx_order_status_history_order_id
    ON order_status_history(order_id);

CREATE INDEX idx_order_status_history_created_at
    ON order_status_history(created_at);



CREATE TABLE order_sagas (
                             id BIGSERIAL PRIMARY KEY,

                             order_id BIGINT NOT NULL,

                             inventory_reservation_id UUID,

                             payment_id BIGINT,

                             current_step VARCHAR(50)
                                             NOT NULL,

                             saga_status VARCHAR(30)
                                             NOT NULL,

                             failure_reason TEXT,

                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP,

                             CONSTRAINT fk_order_sagas_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id),

                             CONSTRAINT uk_order_sagas_order_id
                                 UNIQUE (order_id)
);


CREATE INDEX idx_order_sagas_saga_status
    ON order_sagas(saga_status);

CREATE INDEX idx_order_sagas_current_step
    ON order_sagas(current_step);