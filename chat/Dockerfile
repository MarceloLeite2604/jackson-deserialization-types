FROM maven:3.8.6-eclipse-temurin-19-alpine as build
WORKDIR /tmp/build
COPY pom.xml .
COPY src src
RUN --mount=type=cache,target=/root/.m2,id=maven-cache,sharing=locked \
  mvn -B package

FROM eclipse-temurin:19-alpine as jre-build
WORKDIR /tmp/jre-build
COPY ./docker/jre-build/create-java-runtime.sh create-java-runtime.sh
RUN chmod u+xw create-java-runtime.sh; ./create-java-runtime.sh

FROM alpine:3.17.2 as project
ENV JAVA_HOME=/opt/java/openjdk
ENV JAVA_OPTS="-Xms64M -Xmx128M"
RUN mkdir -p /opt/project $JAVA_HOME
RUN chown -R 1001:1001 /opt/project $JAVA_HOME
USER 1001:1001
COPY --chown=1001:1001 --from=jre-build /tmp/java-runtime $JAVA_HOME
COPY --chown=1001:1001 --from=build /tmp/build/target/*.jar /opt/project/app.jar
COPY --chown=1001:1001 docker/project/entrypoint.sh /opt/project/entrypoint.sh
RUN chmod ug+xw /opt/project/entrypoint.sh
ENTRYPOINT ["/opt/project/entrypoint.sh"]