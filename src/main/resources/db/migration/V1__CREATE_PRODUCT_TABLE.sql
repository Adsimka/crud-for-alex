-- Create product table

CREATE TABLE product (
    id UUID PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    description VARCHAR(50)
);