CREATE TABLE inventory
(
    id         UUID PRIMARY KEY,
    product_id UUID                     NOT NULL,
    on_hand    INTEGER                  NOT NULL,
    reserved   INTEGER                  NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_inventory_product
        UNIQUE (product_id),

    CONSTRAINT ck_inventory_on_hand
        CHECK (on_hand >= 0),

    CONSTRAINT ck_inventory_reserved
        CHECK (reserved >= 0),

    CONSTRAINT ck_inventory_reserved_on_hand
        CHECK (reserved <= on_hand)
);

CREATE INDEX idx_inventory_product_id
    ON inventory (product_id);