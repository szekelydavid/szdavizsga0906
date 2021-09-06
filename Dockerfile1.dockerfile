FROM openjdk:11.0.11-jre-slim-buster
WORKDIR /app
EXPOSE 8080
COPY target/cookbook-0.0.1-SNAPSHOT.jar /app/cookbook.jar
CMD "java" "-jar" "musicapi.jar"