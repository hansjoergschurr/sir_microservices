FROM openjdk:11-jdk-slim
RUN addgroup --system spring 
RUN adduser --system --ingroup spring spring 
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV SERVER_PORT=8080
ENV AUTH_URL=http://localhost:8081
ENV PROFILE_URL=http://localhost:8082
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=${SERVER_PORT}", "--service.profile=${PROFILE_URL}", "--service.authentication=${AUTH_URL}"]
