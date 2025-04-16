--liquibase formatted sql

--changeset adsimka:1
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

--changeset adsimka:2
CREATE TABLE IF NOT EXISTS product (
    id UUID DEFAULT gen_random_uuid(),
    name VARCHAR(20) NOT NULL,
    description VARCHAR(50),
    CONSTRAINT product_pkey PRIMARY KEY (id)
);