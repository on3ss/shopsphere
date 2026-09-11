CREATE TABLE orders
(
    id           UUID PRIMARY KEY,
    customer_id  VARCHAR(255)   NOT NULL,
    status       VARCHAR(32)    NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    currency     VARCHAR(3)     NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL,
    updated_at   TIMESTAMPTZ    NOT NULL,

    CONSTRAINT orders_total_amount_positive
        CHECK (total_amount >= 0),

    CONSTRAINT orders_currency_format
        CHECK (char_length(currency) = 3)
);

CREATE INDEX idx_orders_customer_id
    ON orders (customer_id);

CREATE INDEX idx_orders_status
    ON orders (status);


CREATE TABLE order_items
(
    id           UUID PRIMARY KEY,
    order_id     UUID           NOT NULL,
    product_id   UUID           NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    unit_price   NUMERIC(19, 2) NOT NULL,
    currency     VARCHAR(3)     NOT NULL,
    quantity     INTEGER        NOT NULL,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders (id)
            ON DELETE CASCADE,

    CONSTRAINT order_items_quantity_positive
        CHECK (quantity > 0),

    CONSTRAINT order_items_unit_price_positive
        CHECK (unit_price >= 0),

    CONSTRAINT order_items_currency_format
        CHECK (char_length(currency) = 3)
);

CREATE INDEX idx_order_items_order_id
    ON order_items (order_id);

CREATE INDEX idx_order_items_product_id
    ON order_items (product_id);