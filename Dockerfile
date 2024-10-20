FROM maven:amazoncorretto AS build

RUN mkdir /src
COPY . /src

WORKDIR /src

RUN mvn package

FROM amazoncorretto:17.0.7-alpine

ARG APPLICATION_USER=appuser
RUN adduser -u 1000 -D $APPLICATION_USER

RUN mkdir /app && \
    chown -R $APPLICATION_USER /app

RUN mkdir /app/h2 && \
    chown -R $APPLICATION_USER /app/h2

USER 1000

COPY --from=build --chown=1000:1000 /src/target/back-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app

EXPOSE 80
ENTRYPOINT [ "java", "-Dserver.port=80", "-jar", "/app/app.jar"]
