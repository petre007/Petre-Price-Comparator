CREATE EXTERNAL TABLE product_catalog (
  product_id BIGINT,
  currency STRING,
  discount_added_date STRING,
  discount_expiry_date STRING,
  discount_percentage DOUBLE,
  discount_starting_date STRING,
  package_quantity INT,
  package_unit STRING,
  price DOUBLE,
  product_name STRING
)
PARTITIONED BY (
  year STRING,
  month STRING,
  day STRING,
  shop STRING,
  product_category STRING,
  brand STRING
)
STORED AS PARQUET
LOCATION 's3://petre-price-comparator-12345/price_catalog_data/';

-- And then RUN this:
-- MSCK REPAIR TABLE product_catalog;