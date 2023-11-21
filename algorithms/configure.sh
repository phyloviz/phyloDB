#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
SCRIPT_NAME=$(basename "$0")

Help()
{
    # Display Help
    echo "Usage: ./$SCRIPT_NAME [options]"
    echo "Description: Configure and build the algorithms code JAR."
    echo ""
    echo ""
    echo "Options:"
    echo "-c     Check tool versions."
    echo "-s     Skip JAR file building in case it already exists."
    echo "-t     Run mvn build tests."
    echo "-v     Verbose mode."
    echo "-h     Print this Help."
    echo
}

# Process arguments.
CHECK_VERSIONS=false
SKIP_REBUILD_JARS=false
RUN_MVN_TESTS=false
VERBOSE_MODE=false
while getopts :cstvh flag
do
    case "${flag}" in
        c) CHECK_VERSIONS=true;;
        s) SKIP_REBUILD_JARS=true;;
        t) RUN_MVN_TESTS=true;;
        v) VERBOSE_MODE=true;;
        h) #display Help
            Help
            exit;;
    esac
done

source "$SCRIPT_DIR/../util.sh"
if [ $CHECK_VERSIONS = true ]; then
    check_java
    check_maven
fi

ALGO_NAME=$(get_algorithms_name)
ALGO_VERSION=$(get_algorithms_version)
ALGO_DIR="$SCRIPT_DIR"
ALGO_JAR_BASE_NAME="$ALGO_NAME-$ALGO_VERSION.jar"
ALGO_JAR="$ALGO_DIR/target/$ALGO_JAR_BASE_NAME"

if [ ! -f "$ALGO_JAR" ] || [ $SKIP_REBUILD_JARS = false ] ; then
    echo "[$SCRIPT_NAME][INFO] - Building $ALGO_NAME: $ALGO_JAR."
    pushd "$ALGO_DIR"
    if [ $RUN_MVN_TESTS = true ]; then
        if [ $VERBOSE_MODE = true ]; then
            mvn package 
        else
            mvn package > "$ALGO_DIR/build.log" 2>&1
        fi
    else
        if [ $VERBOSE_MODE = true ]; then
            mvn package -Dmaven.test.skip=true
        else
            mvn package -Dmaven.test.skip=true > "$ALGO_DIR/build.log" 2>&1
        fi
    fi
    popd 
else
    echo "[$SCRIPT_NAME][INFO] - Found $ALGO_JAR_BASE_NAME, skipping build."
fi