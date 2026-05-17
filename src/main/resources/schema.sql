CREATE TABLE IF NOT EXISTS orders (
    id          BIGSERIAL PRIMARY KEY,
    user_id     VARCHAR(100)   NOT NULL,
    product_id  BIGINT         NOT NULL,
    quantity    INT            NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);
