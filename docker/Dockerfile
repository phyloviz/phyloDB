FROM openjdk:11

# Argument-passing on image build.
# See: https://docs.docker.com/engine/reference/builder/#arg
ARG phylodb_version

COPY phylodb/build/libs/phylodb-$phylodb_version.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
