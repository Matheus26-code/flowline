CREATE TABLE sector(
        id                      BIGSERIAL PRIMARY KEY,
        name                    VARCHAR(50)     NOT NULL,
        description             VARCHAR(1000)     NOT NULL,
        building                VARCHAR(100),
        warehouse_id BIGINT     NOT NULL REFERENCES warehouse(id),
        responsible_id BIGINT   NOT NULL REFERENCES users(id)
);