#!/bin/bash

SCRIPT_DIR=$(dirname $(readlink -f $0))
SCRIPT_NAME=$(basename "$0")

fn_exists() {
  # appended double quote is an ugly trick to make sure we do get a string -- if $1 is not a known command, type does not output anything
  [ `type -t $1`"" == 'function' ]
}
export -f fn_exists

pushd () {
    command pushd "$@" > /dev/null
}
export -f pushd

popd () {
    command popd "$@" > /dev/null
}
export -f popd

# Based on: https://stackoverflow.com/a/28926650
ingroup () { 
    [[ " `id -Gn ${2-}` " == *" $1 "* ]]; 
}
export -f ingroup

# Check presence of Java/JDK.
check_java () {
    if ! command -v java &> /dev/null
    then
        echo "[$SCRIPT_NAME][ERROR] - java could not be found. Please ensure Java 8 or greater is available. Exiting."
        exit 1
    fi
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1$2}')
    if [ "$JAVA_VERSION" -ge 18 ] ; then
        echo "[$SCRIPT_NAME][INFO] - Java version: $JAVA_VERSION."
    else
        echo "[$SCRIPT_NAME][ERROR] - Java version must be greater or equal to 8. Exiting."
        exit 1
    fi
}
export -f check_java

# Check presence of Maven.
check_maven () {
    if ! command -v mvn &> /dev/null
    then
        echo "[$SCRIPT_NAME][ERROR] - 'mvn' could not be found. Please install it. Exiting."
        exit 1
    else 
        MVN_VERSION=$(mvn -v | grep Apache | cut -d' ' -f3-4)
        echo "[$SCRIPT_NAME][INFO] - Maven version: $MVN_VERSION."
    fi    
}
export -f check_maven

# Check presence of Gradle.
check_gradle() {
    if ! command -v ./phylodb/gradlew &> /dev/null
    then
        echo "[$SCRIPT_NAME][ERROR] - 'gradle' could not be found. Please install it. Exiting."
        exit 1
    else
        GRADLE_VERSION=$(./phylodb/gradlew -v | grep Gradle | cut -d' ' -f2)
        echo "[$SCRIPT_NAME][INFO] - Gradle version: $GRADLE_VERSION."
    fi
}
export -f check_gradle

# Check presence of Docker.
check_docker() {
    if ! command -v docker &> /dev/null
    then
        echo "[$SCRIPT_NAME][ERROR] - 'docker' could not be found. Please install it. Exiting."
        exit 1
    fi
    DOCKER_VERSION=$(docker -v | cut -d' ' -f3-5)
    echo "[$SCRIPT_NAME][INFO] - Docker version: $DOCKER_VERSION."

    # Check if the Docker daemon is running.
    docker version > /dev/null 2>&1
    ret_status=$?
    if [ $ret_status -eq 0 ]; then
        echo "[$SCRIPT_NAME][INFO] - Docker daemon is running."
    else
        echo "[$SCRIPT_NAME][ERROR] - Docker daemon is not running. Please launch it. Exiting."
        exit 1
    fi

    # Check if current user is in 'docker' group.
    if ingroup "docker"; then
        echo "[$SCRIPT_NAME][INFO] - User '$USER' is in group 'docker'."
    else
        echo "[$SCRIPT_NAME][ERROR] - User '$USER' is not in group 'docker', please check with your administrator. Exiting."
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null
    then
        echo "[$SCRIPT_NAME][ERROR] - 'docker-compose' could not be found. Please install it. Exiting."
        exit 1
    fi

}
export -f check_docker

get_docker_build_command() {
    DOCKER_CMD_INFO=$(docker buildx 2>&1 | head -n 1 | grep 'not a docker command')
    if [[ $string == *"not a docker command"* ]]; then
        echo "build"
    else
        echo "buildx"
    fi
}
export -f get_docker_build_command

get_algorithms_name() {
    SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
    pushd "$SCRIPT_DIR/algorithms"
    ALGO_NAME=$(cat pom.xml | grep "artifactId" | cut -d'>' -f2 | cut -d'<' -f1 | head -n 1)
    popd
    echo "$ALGO_NAME"
}
export -f get_algorithms_name

get_algorithms_version() {
    SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
    pushd "$SCRIPT_DIR/algorithms"
    ALGO_VERSION=$(cat pom.xml | grep "version" | cut -d'>' -f2 | cut -d'<' -f1 | head -n 1)
    popd
    echo "$ALGO_VERSION"
}
export -f get_algorithms_version

get_phylodb_name() {
    SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
    pushd "$SCRIPT_DIR/phylodb"
    PHYLODB_NAME=$(cat settings.gradle | cut -d' ' -f3 | cut -d "'" -f2)
    popd
    echo "$PHYLODB_NAME"
}
export -f get_phylodb_name

get_phylodb_version() {
    SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
    pushd "$SCRIPT_DIR/phylodb"
    PHYLODB_VERSION=$(cat build.gradle | grep "version = " | cut -d'=' -f2 | cut -d"'" -f2)
    popd
    echo "$PHYLODB_VERSION"
}
export -f get_phylodb_version

get_latest_stable_neo4j_version() {
    NEO4J_APOC_URL="https://github.com/neo4j/apoc/releases/latest"
    
    # Need to go through URL redirect. Based on: https://stackoverflow.com/a/5300429
    NEO4J_LATEST_APOC_URL=$(curl "$NEO4J_APOC_URL" -s -L -I -o /dev/null -w '%{url_effective}')
    NEO4J_LATEST_VERSION=$(echo "$NEO4J_LATEST_APOC_URL" | awk -F/ '{print $NF}')
    echo "$NEO4J_LATEST_VERSION"
}
export -f get_latest_stable_neo4j_version
