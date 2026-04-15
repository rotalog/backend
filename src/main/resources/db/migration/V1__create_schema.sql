-- =====================================================
-- V1__create_schema.sql
-- Criação do schema inicial do banco de dados Rotalog
-- =====================================================

-- -----------------------------------------------
-- PRODUCTS
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(150)    NOT NULL,
    description     VARCHAR(500),
    price           NUMERIC(10, 2)  NOT NULL,
    category        VARCHAR(100),
    unit_of_measure VARCHAR(50),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP
);

-- -----------------------------------------------
-- CUSTOMERS
-- -----------------------------------------------
CREATE TYPE customer_type AS ENUM ('INDIVIDUAL', 'COMPANY');

CREATE TABLE IF NOT EXISTS customers (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(150)    NOT NULL,
    tax_id      VARCHAR(14)     UNIQUE,
    type        VARCHAR(20)     NOT NULL DEFAULT 'INDIVIDUAL',
    email       VARCHAR(150),
    phone       VARCHAR(20),
    address     VARCHAR(200),
    city        VARCHAR(100),
    state       CHAR(2),
    birth_date  DATE,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL,
    updated_at  TIMESTAMP
);

-- -----------------------------------------------
-- DISTRIBUTORS
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS distributors (
    id              BIGSERIAL       PRIMARY KEY,
    legal_name      VARCHAR(150)    NOT NULL,
    trade_name      VARCHAR(150),
    tax_id          VARCHAR(18)     UNIQUE,
    address         VARCHAR(200),
    city            VARCHAR(100),
    state           CHAR(2),
    phone           VARCHAR(20),
    email           VARCHAR(150),
    contact_person  VARCHAR(200),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP
);

-- -----------------------------------------------
-- ORDERS
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id              BIGSERIAL           PRIMARY KEY,
    customer_id     BIGINT              NOT NULL REFERENCES customers(id),
    distributor_id  BIGINT              NOT NULL REFERENCES distributors(id),
    status          VARCHAR(20)         NOT NULL DEFAULT 'PENDING',
    notes           VARCHAR(500),
    total_amount    NUMERIC(12, 2)      NOT NULL DEFAULT 0,
    created_at      TIMESTAMP           NOT NULL,
    updated_at      TIMESTAMP
);

-- -----------------------------------------------
-- ORDER ITEMS
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id          BIGSERIAL       PRIMARY KEY,
    order_id    BIGINT          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id  BIGINT          NOT NULL REFERENCES products(id),
    quantity    INTEGER         NOT NULL,
    unit_price  NUMERIC(10, 2)  NOT NULL,
    subtotal    NUMERIC(12, 2)  NOT NULL
);

-- -----------------------------------------------
-- INVENTORY
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS inventory (
    id                  BIGSERIAL   PRIMARY KEY,
    product_id          BIGINT      NOT NULL UNIQUE REFERENCES products(id),
    current_quantity    INTEGER     NOT NULL DEFAULT 0,
    minimum_quantity    INTEGER     NOT NULL DEFAULT 0,
    created_at          TIMESTAMP   NOT NULL,
    updated_at          TIMESTAMP
);

-- -----------------------------------------------
-- INVENTORY MOVEMENTS
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS inventory_movements (
    id                  BIGSERIAL       PRIMARY KEY,
    product_id          BIGINT          NOT NULL REFERENCES products(id),
    type                VARCHAR(10)     NOT NULL,  -- INBOUND | OUTBOUND | ADJUSTMENT
    quantity            INTEGER         NOT NULL,
    reason              VARCHAR(300),
    previous_balance    INTEGER         NOT NULL,
    current_balance     INTEGER         NOT NULL,
    order_id            BIGINT          REFERENCES orders(id),
    created_at          TIMESTAMP       NOT NULL
);

-- -----------------------------------------------
-- INDEXES
-- -----------------------------------------------
CREATE INDEX idx_products_category    ON products(category);
CREATE INDEX idx_products_active      ON products(active);

CREATE INDEX idx_customers_tax_id     ON customers(tax_id);
CREATE INDEX idx_customers_active     ON customers(active);

CREATE INDEX idx_distributors_tax_id  ON distributors(tax_id);
CREATE INDEX idx_distributors_active  ON distributors(active);

CREATE INDEX idx_orders_customer      ON orders(customer_id);
CREATE INDEX idx_orders_distributor   ON orders(distributor_id);
CREATE INDEX idx_orders_status        ON orders(status);
CREATE INDEX idx_orders_created_at    ON orders(created_at);

CREATE INDEX idx_order_items_order    ON order_items(order_id);
CREATE INDEX idx_order_items_product  ON order_items(product_id);

CREATE INDEX idx_inv_mov_product      ON inventory_movements(product_id);
CREATE INDEX idx_inv_mov_type         ON inventory_movements(type);
CREATE INDEX idx_inv_mov_created_at   ON inventory_movements(created_at);
