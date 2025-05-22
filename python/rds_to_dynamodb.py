import sys
import json
import base64
import boto3
from botocore.exceptions import ClientError
from pyspark.context import SparkContext
from pyspark.sql.functions import lit, col
from awsglue.context import GlueContext
from awsglue.utils import getResolvedOptions
from awsglue.job import Job
from awsglue.dynamicframe import DynamicFrame

# --- Init Glue job ---
args = getResolvedOptions(sys.argv, ['JOB_NAME'])
sc = SparkContext()
glueContext = GlueContext(sc)
spark = glueContext.spark_session
job = Job(glueContext)
job.init(args['JOB_NAME'], args)

# --- Load RDS credentials from Secrets Manager ---
def get_secret(secret_name, region_name="eu-central-1"):
    session = boto3.session.Session()
    client = session.client(service_name='secretsmanager', region_name=region_name)

    try:
        get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    except ClientError as e:
        raise Exception(f"Error retrieving secret {secret_name}: {e}")

    if 'SecretString' in get_secret_value_response:
        return json.loads(get_secret_value_response['SecretString'])
    else:
        return json.loads(base64.b64decode(get_secret_value_response['SecretBinary']))

# Replace this with your actual secret name
secret = get_secret("rds-credentials")
rds_user = secret["username"]
rds_password = secret["password"]
rds_dbname = secret["dbname"]

# --- Define RDS and DynamoDB settings ---
jdbc_url = "jdbc:postgresql://pricecomparatordb.cp4oqwqw6yh7.eu-central-1.rds.amazonaws.com/" + rds_dbname
dynamodb_table = "PriceCatalogDynamoDb"

# --- Load and tag data from a single RDS table ---
def load_table_with_shop(table_name, shop_name):
    df = glueContext.create_dynamic_frame.from_options(
        connection_type="postgresql",
        connection_options={
            "url": jdbc_url,
            "dbtable": table_name,
            "user": rds_user,
            "password": rds_password
        }
    ).toDF()
    return df.withColumn("shop", lit(shop_name))

# --- Load all 3 source tables and append 'shop' field ---
lidl_df = load_table_with_shop("lidl", "LIDL")
kaufland_df = load_table_with_shop("kaufland", "KAUFLAND")
profi_df = load_table_with_shop("profi", "PROFI")

# --- Union all data ---
combined_df = lidl_df.unionByName(kaufland_df).unionByName(profi_df)

# --- Ensure primary key types match DynamoDB schema ---
combined_df = combined_df.withColumn("product_id", col("product_id").cast("long"))
combined_df = combined_df.withColumn("shop", col("shop").cast("string"))

# --- Convert back to Glue DynamicFrame ---
combined_dyf = DynamicFrame.fromDF(combined_df, glueContext, "combined_dyf")

# --- Write to DynamoDB ---
glueContext.write_dynamic_frame_from_options(
    frame=combined_dyf,
    connection_type="dynamodb",
    connection_options={
        "dynamodb.output.tableName": dynamodb_table,
        "dynamodb.throughput.write.percent": "1.0"
    }
)

# --- Commit job ---
job.commit()
