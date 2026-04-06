CREATE TABLE users(
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)     NOT NULL,
    email       VARCHAR(50)     NOT NULL,
    password    VARCHAR(255)     NOT NULL,
    role        VARCHAR(15)     NOT NULL,
    warehouse_id BIGINT NOT NULL REFERENCES warehouse(id)
);