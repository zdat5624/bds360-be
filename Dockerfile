# --- Stage 1: Build Stage ---
# Sử dụng image Maven với JDK 21 để build project
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy file cấu hình Maven và source code
COPY pom.xml .
COPY src ./src

# Build project, bỏ qua chạy Unit Test để nhanh hơn
RUN mvn clean package -DskipTests

# --- Stage 2: Run Stage ---
# Sử dụng JRE 21 bản Alpine siêu nhẹ để chạy ứng dụng
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file jar đã build từ Stage 1 sang Stage 2
# Lưu ý: target/*.jar sẽ lấy file jar chính của bác
COPY --from=build /app/target/*.jar app.jar

# Render sẽ cấp cổng ngẫu nhiên qua biến môi trường PORT
EXPOSE 10000

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]