# At this point, BUILDER stage should have your .jar or whatever in some path
FROM openjdk:11-slim

LABEL maintainer="akalu"
LABEL com.cloud.name = "eureka-service"
LABEL com.cloud.version = "1.0.0"

WORKDIR /opt/app
COPY ./target/cloud-eureka-1.0.0-SNAPSHOT.jar .
EXPOSE 8761
CMD [ "java", "-jar", "/opt/app/cloud-eureka-1.0.0-SNAPSHOT.jar" ]