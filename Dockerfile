FROM gradle:6.8.3-jdk11-openj9 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --debug

FROM openjdk:11

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/app/build/libs/*.jar /app/ceres.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/ceres.jar"]
