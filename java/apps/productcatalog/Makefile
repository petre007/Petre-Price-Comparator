# Variables - update if needed
AWS_REGION := eu-central-1
ECR_REPO_NAME := price-comparator-backend
IMAGE_TAG := productcatalog-app
ECR_URI := 

# Build the Spring Boot JAR and Docker image
build:
	mvn clean package
	docker build -t $(ECR_REPO_NAME):$(IMAGE_TAG) .

# Tag the image for ECR
tag:
	docker tag $(ECR_REPO_NAME):$(IMAGE_TAG) $(ECR_URI):$(IMAGE_TAG)

# Authenticate Docker with ECR
login:
	aws ecr get-login-password --region $(AWS_REGION) | docker login --username AWS --password-stdin $(ECR_URI)

# Push the image to ECR
push: login tag
	docker push $(ECR_URI):$(IMAGE_TAG)

# Build, tag, login, and push
deploy: build push
