#!/bin/bash

SCRIPT_DIR=$(dirname $(readlink -f $0))
SCRIPT_NAME=$(basename "$0")

pushd () {
    command pushd "$@" > /dev/null
}
export -f pushd

popd () {
    command popd "$@" > /dev/null
}
export -f popd

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
        echo "[$SCRIPT_NAME][ERROR] - Java version must be greater or equal to 8."
        exit 1
    fi
}
export -f check_java

# Check presence of Maven.
check_maven () {
    if ! command -v mvn &> /dev/null
    then
        echo "[$SCRIPT_NAME][ERROR] - mvn could not be found. Please install it. Exiting."
        exit 1
    else 
        MVN_VERSION=$(mvn -v | grep Apache | cut -d' ' -f3-4)
        echo "[$SCRIPT_NAME][INFO] - Maven version: $MVN_VERSION."
    fi    
}
export -f check_maven

# Check presence of Gradle.
check_gradle() {
    if ! command -v gradle &> /dev/null
    then
        echo "[$SCRIPT_NAME][ERROR] - gradle could not be found. Please install it. Exiting"
        exit 1
    else
        GRADLE_VERSION=$(gradle -v | grep Gradle | cut -d' ' -f2)
        echo "[$SCRIPT_NAME][INFO] - Gradle version: $GRADLE_VERSION."
    fi
}
export -f check_gradle






