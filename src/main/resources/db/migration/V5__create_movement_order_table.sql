CREATE TABLE orders(

    id                              BIGSERIAL PRIMARY KEY,
    origin_sector_id BIGINT         NOT NULL REFERENCES sector(id),
    destination_sector_id BIGINT    NOT NULL REFERENCES sector(id),
    user_id BIGINT                  NOT NULL REFERENCES users(id),
    product_id BIGINT               NOT NULL REFERENCES product(id),
    status                          VARCHAR(20) NOT NULL,
    quantity                        NUMERIC(10,3) NOT NULL DEFAULT 0,
    created_at                      TIMESTAMP NOT NULL
);