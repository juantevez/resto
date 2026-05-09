# Etapa de ejecución con Java 21 (Temurin es excelente para Ubuntu)
FROM eclipse-temurin:21-jre-jammy

# Directorio donde vivirá la app en el contenedor
WORKDIR /app

# Copiamos el JAR generado (asegúrate de que el nombre coincida con tu pom.xml)
COPY target/kitchen-1.0.0.jar app.jar

# Exponemos el puerto de cocina
EXPOSE 8083

# Ejecución activando Virtual Threads
ENTRYPOINT ["java", "-Dspring.threads.virtual.enabled=true", "-jar", "app.jar"]
