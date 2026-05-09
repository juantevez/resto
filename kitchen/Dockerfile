# Etapa de ejecución
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Usamos un comodín para asegurarnos de traer el JAR correcto
# o renombramos el archivo en el POM
COPY target/*.jar app.jar

EXPOSE 8083

# El flag de virtual threads ya lo pusimos en el application.yml,
# pero dejarlo acá no hace daño (es redundante pero seguro).
ENTRYPOINT ["java", "-jar", "app.jar"]