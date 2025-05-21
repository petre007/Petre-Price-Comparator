# Use Amazon Corretto 17 as the base image
FROM amazoncorretto:21-alpine

# Set working directory
WORKDIR /app

# Copy the Spring Boot fat JAR into the container
COPY target/discounts-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app will run on
EXPOSE 80

# Run the Spring Boot app with injected environment variables
ENTRYPOINT ["sh", "-c", "java -jar app.jar \
  --spring.datasource.url=${DATASOURCE_URL} \
  --spring.datasource.username=${DB_USERNAME} \
  --spring.datasource.password=${DB_PASSWORD} \
  --security.pc-api-key=${PC_API_KEY} \
  --pc-base-url=${PC_BASE_URL}"]
