FROM amazoncorretto:17-alpine-jdk

ENV SPRING_DB_DRIVE=org.drive.MariaDb
ENV SPRING_DB_URL=localhost
ENV SPRING_DB_USER=root
ENV SPRING_DB_PASSWORD=root
ENV SPRING_DB_SCHEMA=''

COPY target/financial-system-0.0.1-SNAPSHOT.jar financial-system.jar
ENTRYPOINT ["java","-jar","/financial-system.jar"]
