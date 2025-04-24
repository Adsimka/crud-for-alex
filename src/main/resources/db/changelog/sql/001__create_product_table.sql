-- Liquibase formatted sql
-- DBMS: postgres
-- Fail: Check database connection or permissions. For extension errors, ensure PostgreSQL version supports pgcrypto.
-- Labels: table,product

-- Changeset adsimka:00101
-- Comment: Add pgcrypto extension for backward compatibility (PostgreSQL < 13)
-- Date: 2025-04-24
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Changeset adsimka:00102
-- Comment: Create table for products
-- Date: 2025-04-24
CREATE TABLE IF NOT EXISTS public.product
(
    id              UUID DEFAULT gen_random_uuid(), -- Generated UUID primary key
    name            VARCHAR(20) NOT NULL,           -- Max 20 chars for compact display (business requirement)
    description     VARCHAR(50),                    -- Optional brief description, max 50 chars (business requirement)
    CONSTRAINT product_pkey PRIMARY KEY (id)
);