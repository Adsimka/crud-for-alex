-- Liquibase formatted sql
-- DBMS: postgres
-- Fail: Check database connection or permissions
-- Labels: table,product_variant

--Changeset adsimka:00401
--Comment: Create table for product variants
-- Date: 2026-11-26
CREATE TABLE IF NOT EXISTS public.product_variant
(
    id         UUID DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,        -- Generated UUID primary key
    code       VARCHAR(20) NOT NULL, -- Max 20 chars for compact display (business requirement)
    name       VARCHAR(50) NOT NULL, -- Optional brief description, max 50 chars (business requirement)
    CONSTRAINT product_variant_pkey PRIMARY KEY (id),
    CONSTRAINT fk_product_variant_product FOREIGN KEY (product_id) REFERENCES public.product (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_product_variant_product_id ON public.product_variant (product_id);

COMMENT ON TABLE public.product_variant IS 'Selectable configurations of a product';
COMMENT ON COLUMN public.product_variant.product_id IS 'Product ID';
COMMENT ON COLUMN public.product_variant.code IS 'Product variant code';
COMMENT ON COLUMN public.product_variant.name IS 'Product variant name';
