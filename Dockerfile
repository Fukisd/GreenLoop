FROM openjdk:21-jdk-slim

WORKDIR /app

# Install Maven directly instead of using wrapper
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .

# Download dependencies with retry and timeout settings
RUN mvn dependency:go-offline -B \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.http.retryHandler.requestSentEnabled=true \
    -Dmaven.wagon.http.retryHandler.retryInterval=1000

# Copy source code
COPY src src

# Build application with optimized settings
RUN mvn clean package -DskipTests -B \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true \
    -Dmaven.source.skip=true \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.http.retryHandler.requestSentEnabled=true \
    -Dmaven.wagon.http.retryHandler.retryInterval=1000

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "target/circular-fashion-1.0.0.jar"] 