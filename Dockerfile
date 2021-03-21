FROM gradle:6.8.3-jdk11-openj9 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:11

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/ceres-0.0.1-SNAPSHOT.jar /app/ceres-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/ceres-0.0.1-SNAPSHOT.jar"]
