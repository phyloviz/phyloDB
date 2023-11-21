#!/bin/bash

fn_exists() {
  # appended double quote is an ugly trick to make sure we do get a string -- if $1 is not a known command, type does not output anything
  [ `type -t $1`"" == 'function' ]
}

Help()
{
    # Display Help
    echo "Usage: ./configure.sh [options]"
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

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
SCRIPT_NAME=$(basename "$0")

source "$SCRIPT_DIR/../util.sh"
if [ $CHECK_VERSIONS = true ]; then
    check_java
    check_maven
fi

ALGO_VERSION=$(cat pom.xml | grep "version" | cut -d'>' -f2 | cut -d'<' -f1 | head -n 1)
ALGO_NAME="algorithms"
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