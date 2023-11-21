#!/bin/bash

Help()
{
    # Display Help
    echo "Usage: ./configure.sh [options]"
    echo "Description: Configure and build the algorithms and phylodb code directories."
    echo ""
    echo ""
    echo "Options:"
    echo "-t     Run mvn build tests."
    echo "-s     Skip JAR file building in case they already exist."
    echo "-h     Print this Help."
    echo
}

# Process arguments.
RUN_MVN_TESTS=false
SKIP_REBUILD_JARS=false
while getopts :tsh flag
do
    case "${flag}" in
        t) RUN_MVN_TESTS=true;;
        s) SKIP_REBUILD_JARS=true;;
        h) #display Help
            Help
            exit;;
    esac
done

source "util.sh"

check_java
check_maven
check_gradle

pushd "algorithms"
. "configure.sh"
popd

pushd "phylodb"
. "configure.sh"
popd

check_docker

pushd "docker"
. "build-docker.sh"
popd
