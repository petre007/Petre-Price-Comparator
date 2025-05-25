import sys
from datetime import datetime
from awsglue.utils import getResolvedOptions
from pyspark.context import SparkContext
from awsglue.context import GlueContext
from awsglue.job import Job
from pyspark.sql.functions import lit

# Parse Glue job arguments
args = getResolvedOptions(sys.argv, ["JOB_NAME"])
sc = SparkContext()
glueContext = GlueContext(sc)
spark = glueContext.spark_session
job = Job(glueContext)
job.init(args["JOB_NAME"], args)

# === SETTINGS ===
S3_OUTPUT_BASE = "s3://petre-price-comparator-12345/price_catalog_data/"

# Get today's date
today = datetime.today()
year = today.strftime("%Y")
month = today.strftime("%m")
day = today.strftime("%d")

# === STEP 1: Load data from DynamoDB ===
dyf = glueContext.create_dynamic_frame.from_options(
    connection_type="dynamodb",
    connection_options={
        "dynamodb.input.tableName": "PriceCatalogDynamoDb",
        "dynamodb.throughput.read.percent": "1.0"
    }
)

# Convert to DataFrame for partitioning
df = dyf.toDF()

# === STEP 2: Add partition columns ===
df = df.withColumn("year", lit(year)) \
       .withColumn("month", lit(month)) \
       .withColumn("day", lit(day))

# === STEP 3: Write to S3 with correct partitioning ===
df.write \
    .mode("append") \
    .partitionBy("year", "month", "day", "shop", "product_category", "brand") \
    .parquet(S3_OUTPUT_BASE)

job.commit()
