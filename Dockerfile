# ============================================================
# Stage 1: Build C++ Core Engine
# ============================================================
FROM gcc:13 AS cpp-builder

WORKDIR /build

# Copy C++ source code
COPY cpp_src/ .

# Compile the C++ executable
RUN g++ -std=c++11 -O2 -static -o mybignumber_core main.cpp src/MyBigNumber.cpp

# ============================================================
# Stage 2: Build Spring Boot Application
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS java-builder

WORKDIR /build

# Copy pom.xml first and download dependencies (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy Java source code and build the JAR
COPY src/ src/
RUN mvn clean package -DskipTests -B

# ============================================================
# Stage 3: Runtime Environment (Lightweight)
# ============================================================
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy C++ executable from Stage 1
COPY --from=cpp-builder /build/mybignumber_core .
RUN chmod +x mybignumber_core

# Copy Spring Boot JAR from Stage 2
COPY --from=java-builder /build/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
