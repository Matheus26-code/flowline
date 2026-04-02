CREATE TABLE product(
        id                      BIGSERIAL PRIMARY KEY,
        name                    VARCHAR(50) NOT NULL,
        weight                  NUMERIC(10, 3) NOT NULL,
        unit                    VARCHAR(100) NOT NULL,
        location                VARCHAR(50) NOT NULL ,
        warehouse_id BIGINT     NOT NULL REFERENCES warehouse(id)
);