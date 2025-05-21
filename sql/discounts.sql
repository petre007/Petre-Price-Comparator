CREATE TABLE lidl_discounts (
                                product_id BIGINT PRIMARY KEY,
                                product_name VARCHAR,
                                product_category VARCHAR,
                                brand VARCHAR,
                                package_quantity VARCHAR,
                                package_unit VARCHAR,
                                from_date TIMESTAMP,
                                to_date TIMESTAMP,
                                percentage_of_discount DOUBLE PRECISION
);

CREATE TABLE kaufland_discounts (
                                    product_id BIGINT PRIMARY KEY,
                                    product_name VARCHAR,
                                    product_category VARCHAR,
                                    brand VARCHAR,
                                    package_quantity VARCHAR,
                                    package_unit VARCHAR,
                                    from_date TIMESTAMP,
                                    to_date TIMESTAMP,
                                    percentage_of_discount DOUBLE PRECISION
);

CREATE TABLE profi_discounts (
                                 product_id BIGINT PRIMARY KEY,
                                 product_name VARCHAR,
                                 product_category VARCHAR,
                                 brand VARCHAR,
                                 package_quantity VARCHAR,
                                 package_unit VARCHAR,
                                 from_date TIMESTAMP,
                                 to_date TIMESTAMP,
                                 percentage_of_discount DOUBLE PRECISION
);
