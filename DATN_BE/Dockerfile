# ---------- Stage 1: Build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# 1. Copy pom.xml và tải thư viện trước (Tận dụng Docker Cache)
COPY pom.xml .
# Dùng lệnh 'mvn' của image, không dùng './mvnw' của máy host để tránh lỗi Windows
RUN mvn dependency:go-offline -B

# 2. Copy source code và build
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy file jar từ stage builder
COPY --from=builder /workspace/target/*.jar app.jar

# Tạo user system để chạy bảo mật (Cross-platform safe)
RUN addgroup --system spring && adduser --system --ingroup spring spring
RUN chown spring:spring /app/app.jar

USER spring

# Cấu hình profile
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java","-jar","app.jar"]