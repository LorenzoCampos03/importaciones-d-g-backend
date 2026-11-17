FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .
COPY src/ src/

# ✅ Dar permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

ENV TNS_ADMIN=/app/Wallet_ImportacionesDG

WORKDIR /app
COPY --from=builder /app/target/importaciones_backend-*.jar app.jar
COPY src/main/Wallet_ImportacionesDG ./Wallet_ImportacionesDG

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
