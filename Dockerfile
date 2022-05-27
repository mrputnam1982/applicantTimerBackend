#Build stage
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
COPY src/main/resources/log4j2-spring.xml /usr/local/lib/
RUN chmod 775 /usr/local/lib/log4j2-spring.xml
RUN echo $(ls -alF /usr/local/lib/)
RUN mvn -f /home/app/pom.xml clean package

#Package Stage
FROM openjdk:11-jre-slim
ARG JAR_FILE=/home/app/target/*.jar
COPY --from=build ${JAR_FILE} /usr/local/lib/app.jar
EXPOSE 8080
RUN echo $(ls -alF /usr/local/lib/)
ENTRYPOINT ["java","-Dlog4j.configurationFile=file:/usr/local/lib/log4j2-spring.xml", "-jar","/usr/local/lib/app.jar"]