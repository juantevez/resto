# Etapa de ejecución
FROM eclipse-temurin:21-jre-jammy

# Directorio de trabajo
WORKDIR /app

# Copiamos el JAR generado por Maven (asegurate que el nombre coincida)
COPY target/ordering-1.0.0.jar app.jar

# Exponemos el puerto (8082 para ordering)
EXPOSE 8082

# Ejecutamos con soporte para Virtual Threads optimizado
ENTRYPOINT ["java", "-Dspring.threads.virtual.enabled=true", "-jar", "app.jar"]
