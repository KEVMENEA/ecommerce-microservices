CREATE TABLE products (
                          id UUID PRIMARY KEY,
                          sku VARCHAR(100) NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price NUMERIC(19,2) NOT NULL,
                          currency VARCHAR(3) NOT NULL,
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          version BIGINT NOT NULL DEFAULT 0,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_products_name
    ON products(name);

CREATE INDEX idx_products_active
    ON products(active);