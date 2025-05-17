variable "aws_region" {
    description = "AWS region to deploy resources"
    type        = string
    default     = "eu-central-1"
}

variable "jar_path" {
  type        = string
  description = "Path to the Lambda JAR file"
  default     = "../java/serverless/RegisterLambda/target/RegisterLambda-1.0-SNAPSHOT.jar"
}
