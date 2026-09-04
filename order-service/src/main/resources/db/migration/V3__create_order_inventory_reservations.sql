CREATE TABLE order_inventory_reservations (
                                              id BIGSERIAL PRIMARY KEY,

                                              order_id BIGINT NOT NULL,

                                              reservation_uuid UUID NOT NULL,

                                              variant_id BIGINT NOT NULL,

                                              warehouse_id BIGINT NOT NULL,

                                              quantity INTEGER NOT NULL,

                                              status VARCHAR(30) NOT NULL,

                                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                              updated_at TIMESTAMP,

                                              CONSTRAINT fk_order_inventory_reservation_order
                                                  FOREIGN KEY (order_id)
                                                      REFERENCES orders(id),

                                              CONSTRAINT uk_order_inventory_reservation_uuid
                                                  UNIQUE (reservation_uuid)
);

CREATE INDEX idx_order_inventory_reservations_order_id
    ON order_inventory_reservations(order_id);