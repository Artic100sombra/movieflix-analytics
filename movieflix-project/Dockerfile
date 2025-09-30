FROM maven:3.9.4-eclipse-temurin-21 AS build
# Aqui separei em duas partes. Primeiro rodamos como build para gerar a build do projeto JAVA
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
# Aqui depois de buildar, copiamos o app.jar pro container e depois rodar o projeto usando o entrypoint quando o container inicia
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
