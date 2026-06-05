FROM eclipse-temurin:17-jdk-jammy

VOLUME /tmp

# Puerto de Pekko Cluster
EXPOSE 2551

ADD target/app.jar app.jar

# Ejecutar con java -jar
# Variables de entorno:
#   HOSTNAME: nombre del contenedor (asignado por Docker / docker-compose)
#   SWITCH_ID: ID del switch (solo para switches)
ENTRYPOINT ["java", "-jar", "app.jar"]
