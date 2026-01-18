CREATE SCHEMA IF NOT EXISTS my_market;

CREATE TABLE IF NOT EXISTS my_market.users
(
    user_id BIGSERIAL PRIMARY KEY ,
    email VARCHAR(128) NOT NULL UNIQUE,
    user_pass VARCHAR(128) NOT NULL ,
    role VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS my_market.brands
(
    brand_id BIGSERIAL PRIMARY KEY ,
    brand_name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS my_market.categories
(
    category_id BIGSERIAL PRIMARY KEY ,
    category_name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS my_market.audits
(
    id BIGSERIAL PRIMARY KEY ,
    created_at TIMESTAMP NOT NULL ,
    created_by VARCHAR(128) REFERENCES my_market.users (email),
    action VARCHAR(32) NOT NULL ,
    is_success VARCHAR(32) NOT NULL ,
    auditable_record VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS my_market.products
(
    id BIGSERIAL PRIMARY KEY ,
    product_name VARCHAR(128) NOT NULL UNIQUE ,
    price DOUBLE PRECISION NOT NULL ,
    category_id BIGINT REFERENCES my_market.categories (category_id) ,
    brand_id BIGINT REFERENCES my_market.brands (brand_id) ,
    description VARCHAR(256) NOT NULL ,
    stock_quantity INTEGER NOT NULL ,
    creation_at TIMESTAMP NOT NULL ,
    modified_at TIMESTAMP CONSTRAINT check_time CHECK (modifiedAt > creationAt)
);