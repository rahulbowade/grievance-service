FROM openjdk:8
MAINTAINER haridas <haridas.kakunje@tarento.com>
ADD target/aurora-0.0.1-SNAPSHOT.jar aurora-0.0.1-SNAPSHOT.jar
ADD public/emails emails
ENTRYPOINT ["java", "-jar", "/aurora-0.0.1-SNAPSHOT.jar"]
EXPOSE 8081

