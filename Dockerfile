# # ===== Build stage =====
# FROM eclipse-temurin:21-jdk AS build
# WORKDIR /app
# COPY . .
# RUN chmod +x ./gradlew
# RUN ./gradlew clean bootJar --no-daemon

# # ===== Run stage =====
# FROM eclipse-temurin:21-jre-alpine
# WORKDIR /app
# COPY --from=build /app/build/libs/*.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","/app/app.jar"]
# ===== Build stage =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 1. Copy Gradle wrapper + build files ก่อน เพื่อ cache dependencies
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# 2. โหลด dependencies (cache)
RUN ./gradlew dependencies --no-daemon || true

# 3. Copy source code ทั้งหมด
COPY . .

# 4. Build jar (skip test เพื่อให้ build เร็วขึ้น)
RUN ./gradlew clean bootJar --no-daemon -x test

# ===== Run stage =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
