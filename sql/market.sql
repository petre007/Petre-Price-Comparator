CREATE TABLE lidl (
    product_id BIGINT PRIMARY KEY,
    product_name VARCHAR,
    product_category VARCHAR,
    brand VARCHAR,
    package_quantity VARCHAR,
    package_unit VARCHAR,
    price DOUBLE PRECISION,
    currency VARCHAR
);

CREATE TABLE kaufland (
    product_id BIGINT PRIMARY KEY,
    product_name VARCHAR,
    product_category VARCHAR,
    brand VARCHAR,
    package_quantity VARCHAR,
    package_unit VARCHAR,
    price DOUBLE PRECISION,
    currency VARCHAR
);

CREATE TABLE profi (
    product_id BIGINT PRIMARY KEY,
    product_name VARCHAR,
    product_category VARCHAR,
    brand VARCHAR,
    package_quantity VARCHAR,
    package_unit VARCHAR,
    price DOUBLE PRECISION,
    currency VARCHAR
);
