#!/bin/bash

Help()
{
    # Display Help
    echo "Usage: ./configure.sh [options]"
    echo "Description: Configure and build the algorithms and phylodb code directories."
    echo ""
    echo ""
    echo "Options:"
    echo "-g     Force the use of the 'gradle' available on \$PATH"
    echo "-s     Skip JAR file building in case they already exist."
    echo "-t     Run mvn build tests."
    echo "-v     Output tool versions."
    echo "-h     Print this Help."
    echo
}

# Process arguments.
FORCE_SYSTEM_GRADLE=false
RUN_MVN_TESTS=false
SKIP_REBUILD_JARS=false
OUTPUT_TOOL_VERSIONS_ONLY=false
while getopts :gstvh flag
do
    case "${flag}" in
        g) FORCE_SYSTEM_GRADLE=true;;
        s) SKIP_REBUILD_JARS=true;;
        t) RUN_MVN_TESTS=true;;
        v) OUTPUT_TOOL_VERSIONS_ONLY=true;;
        h) #display Help
            Help
            exit;;
    esac
done

source "util.sh"

check_java
check_maven

GRADLE_FLAGS=""
if [ "$FORCE_SYSTEM_GRADLE" = true ] ; then
    GRADLE_FLAGS="-g"
fi
check_gradle $GRADLE_FLAGS


if [ $OUTPUT_TOOL_VERSIONS_ONLY = true ]; then
    echo "[$SCRIPT_NAME][INFO] - '-v' was passed, only printed tool versions. Exiting."
    exit 0
fi

echo "[$SCRIPT_NAME][INFO] - Calling algorithms/configure.sh"

pushd "algorithms"
. "configure.sh"
popd

echo "[$SCRIPT_NAME][INFO] - Calling phylodb/configure.sh"

pushd "phylodb"
. "configure.sh" $GRADLE_FLAGS
popd

check_docker

echo "[$SCRIPT_NAME][INFO] - Calling docker/build-docker.sh"

pushd "docker"
. "build-docker.sh"
popd
