FROM maven:3.6.3-openjdk-11 as builder

WORKDIR /
ADD . .
RUN mvn clean package

FROM openjdk:11-jre-buster
RUN apt-get update && apt-get -y install ca-certificates
EXPOSE 8080
WORKDIR /
COPY --from=builder /target/short-link.jar short-link.jar
ENTRYPOINT exec java $JAVA_ARGS $JAVA_OPTS -jar /short-link.jar