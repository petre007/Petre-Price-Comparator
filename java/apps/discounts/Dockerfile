# Use Amazon Corretto 17 as the base image
FROM amazoncorretto:21-alpine

# Set working directory
WORKDIR /app

# Copy the Spring Boot fat JAR into the container
COPY target/market-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app will run on
EXPOSE 80

# Environment variables (can be overridden at runtime)
ENV DATASOURCE_URL=""
ENV DB_USERNAME=""
ENV DB_PASSWORD=""
ENV API_KEY=""
ENV PC_API_KEY=""

# Run the Spring Boot app with injected environment variables
ENTRYPOINT ["sh", "-c", "java -jar app.jar \
  --spring.datasource.url=${DATASOURCE_URL} \
  --spring.datasource.username=${DB_USERNAME} \
  --spring.datasource.password=${DB_PASSWORD}"]
