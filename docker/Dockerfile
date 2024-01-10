ARG OPENJDK_VERSION=18

FROM openjdk:${OPENJDK_VERSION}-alpine AS base
COPY data-generator-1.0-SNAPSHOT-all.jar data-generator-1.0-SNAPSHOT-all.jar
ENTRYPOINT ["java","-jar","data-generator-1.0-SNAPSHOT-all.jar","-ec","100"]

