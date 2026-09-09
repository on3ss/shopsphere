CREATE TABLE products
(
    id          UUID PRIMARY KEY,

    name        VARCHAR(255)   NOT NULL,

    description TEXT           NOT NULL,

    price       NUMERIC(19, 2) NOT NULL,

    currency    VARCHAR(3)        NOT NULL,

    active      BOOLEAN        NOT NULL DEFAULT TRUE,

    created_at  TIMESTAMPTZ    NOT NULL,

    updated_at  TIMESTAMPTZ    NOT NULL,

    CONSTRAINT products_price_positive
        CHECK (price >= 0),

    CONSTRAINT products_currency_format
        CHECK (char_length(currency) = 3)
);

CREATE INDEX idx_products_active
    ON products (active);