CREATE TABLE warehouse(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL,
    description VARCHAR(1000)   NOT NULL,
    street      VARCHAR(100)    NOT NULL,
    city        VARCHAR(50)     NOT NULL,
    state       VARCHAR(100)    NOT NULL,
    zip_code    VARCHAR(15)     NOT NULL
);