# 멀티 스테이지 빌드
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Gradle 파일 복사
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew ./

# 소스 코드 복사
COPY src src

# 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon

# 실행 스테이지
FROM amazoncorretto:17-alpine

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]