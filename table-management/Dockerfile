# Etapa 1: Build (Opcional si ya compilaste con Maven afuera)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
# Aquí se copiaría el código y se compilaría, pero para ir rápido:
COPY target/*.jar app.jar

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/app.jar .

# Habilitar el soporte de Virtual Threads y optimizaciones de red
ENTRYPOINT ["java", "-Dspring.threads.virtual.enabled=true", "-jar", "app.jar"]
