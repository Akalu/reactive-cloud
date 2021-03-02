# At this point, BUILDER stage should have your .jar or whatever in some path
FROM openjdk:11-slim

LABEL maintainer="akalu"
LABEL com.cloud.name = "config-service"
LABEL com.cloud.version = "1.0.0"

WORKDIR /opt/app
COPY ./target/cloud-config-service-1.0.0-SNAPSHOT.jar .
EXPOSE 8888
CMD [ "java", "-jar", "/opt/app/cloud-config-service-1.0.0-SNAPSHOT.jar" ]