-- Liquibase formatted sql
-- DBMS: postgres
-- Fail: Ensure table 'product' exists and is accessible. Verify permissions for COMMENT ON operations.
-- Labels: documentation,product

-- Changeset adsimka:00201
-- Сomment: Add comments to product table and columns
-- Date: 2025-04-24
COMMENT ON TABLE    public.product IS 'Table for storing product information';
COMMENT ON COLUMN   public.product.id IS 'Primary key, generated as UUID';
COMMENT ON COLUMN   public.product.name IS 'Product name';
COMMENT ON COLUMN   public.product.description IS 'Optional short product description';