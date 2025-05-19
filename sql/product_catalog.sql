CREATE TABLE product_catalog (
    product_id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR,
    product_category VARCHAR,
    brand VARCHAR,
    package_quantity VARCHAR,
    package_unit VARCHAR
);