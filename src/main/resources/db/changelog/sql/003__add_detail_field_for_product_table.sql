-- Liquibase formatted sql
-- DBMS: postgres
-- Fail: Check database connection or permissions. Ensure table 'product' exists and is accessible.
-- Labels: documentation,table,product

-- Changeset adsimka:00301
-- Comment: Add new columns to the product table for details and manufacturer info
-- Date: 2025-04-24
ALTER TABLE public.product
    ADD COLUMN specifications VARCHAR(100), -- Specifications of the product, max 100 characters (business requirement)
    ADD COLUMN support_info VARCHAR(100),   -- Support information, max 100 characters (business requirement)
    ADD COLUMN manufacturer_name VARCHAR(50) NOT NULL DEFAULT 'Unknown', -- Name of the manufacturer, max 50 characters (business requirement)
    ADD COLUMN manufacturer_contact VARCHAR(100); -- Contact information of the manufacturer, max 100 characters (business requirement)

-- Changeset adsimka:00302
-- Comment: Add comments to the new columns in the product table
-- Date: 2025-04-24
COMMENT ON COLUMN public.product.specifications IS 'Specifications of the product';
COMMENT ON COLUMN public.product.support_info IS 'Support information for the product';
COMMENT ON COLUMN public.product.manufacturer_name IS 'Name of the manufacturer';
COMMENT ON COLUMN public.product.manufacturer_contact IS 'Contact information of the manufacturer';