#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
SCRIPT_NAME=$(basename "$0")

Help()
{
    # Display Help
    echo "Usage: ./$SCRIPT_NAME [options]"
    echo "Description: Configure and build the phylodb code JAR."
    echo ""
    echo ""
    echo "Options:"
    echo "-c     Check tool versions."
    echo "-d     Download JAR files even if they already exist."
    echo "-u     Upload the image to the Docker repository."
    echo "-v     Verbose mode."
    echo "-h     Print this Help."
    echo
}

# Process arguments.
CHECK_VERSIONS=false
DOWNLOAD_JAR_FORCED=false
VERBOSE_MODE=false
UPLOAD_DOCKER_IMAGE=false
while getopts :cduvh flag
do
    case "${flag}" in
        c) CHECK_VERSIONS=true;;
        d) DOWNLOAD_JAR_FORCED=true;;
        u) UPLOAD_DOCKER_IMAGE=true;;
        v) VERBOSE_MODE=true;;
        h) #display Help
            Help
            exit;;
    esac
done

source "$SCRIPT_DIR/../util.sh"
if [ $CHECK_VERSIONS = true ]; then
    check_docker
fi

# Move into current script's directory.
PROJ_ROOT="$SCRIPT_DIR/.."
pushd "$PROJ_ROOT"

echo "[$SCRIPT_NAME][INFO] - Preparing Docker image files."

# Create Docker drive structure.
INSTANCE_DIR="$PROJ_ROOT/docker/instance"
PLUGINS_DIR="$INSTANCE_DIR/db/plugins"

mkdir -p "$INSTANCE_DIR/db/data"
mkdir -p "$INSTANCE_DIR/db/logs"
mkdir -p "$PLUGINS_DIR"
mkdir -p "$INSTANCE_DIR/app/logs"

ALGO_NAME=$(get_algorithms_name)
ALGO_VERSION=$(get_algorithms_version)
ALGO_DIR="$PROJ_ROOT/$ALGO_NAME"
ALGO_JAR_BASE_NAME="$ALGO_NAME-$ALGO_VERSION.jar"
ALGO_JAR="$ALGO_DIR/target/$ALGO_JAR_BASE_NAME"


# Copy the algorithms JAR file, build first if necessary.
if [ ! -f "$PLUGINS_DIR/$ALGO_JAR_BASE_NAME" ] ; then

    # Check if the source JAR file exists.
    if [ ! -f "$ALGO_JAR" ] ; then
        echo "[$SCRIPT_NAME][INFO] - building $ALGO_JAR."
        pushd "algorithms"
        . "configure.sh"
        popd
    fi

    echo "[$SCRIPT_NAME][INFO] - Copying $ALGO_JAR_BASE_NAME."
    cp "$ALGO_JAR" "$PLUGINS_DIR/"
else
    echo "[$SCRIPT_NAME][INFO] - $PLUGINS_DIR/$ALGO_JAR_BASE_NAME exists."
fi

# Build the phylodb JAR file if necessary.
PHYLODB_NAME=$(get_phylodb_name)
PHYLODB_VERSION=$(get_phylodb_version)
PHYLODB_DIR="$PROJ_ROOT/$PHYLODB_NAME"
PHYLODB_JAR_BASE_NAME="$PHYLODB_NAME-$PHYLODB_VERSION.jar"
PHYLODB_JAR="$PHYLODB_DIR/build/libs/$PHYLODB_JAR_BASE_NAME"
if [ ! -f "$PHYLODB_JAR" ] ; then

    echo "[$SCRIPT_NAME][INFO] - building $PHYLODB_JAR."
    pushd "phylodb"
    . "configure.sh"
    popd
else
    echo "[$SCRIPT_NAME][INFO] - $PHYLODB_JAR already exists."
fi

# Neo4j APOC 5 information:
# https://neo4j.com/docs/apoc/5/installation/

NEO4J_LATEST_VERSION=$(get_latest_stable_neo4j_version)

# Neo4j APOC extended JAR "apoc-VERSION-extended.jar" available here:
# https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/tag/5.13.0
# Download Neo4j APOC 'core' JAR if necessary.
if [ ! -f "$PLUGINS_DIR/apoc-$NEO4J_LATEST_VERSION-core.jar" ] || [ $DOWNLOAD_JAR_FORCED = true ] ; then
    pushd "$PLUGINS_DIR"
    NEO4J_LATEST_APOC_CORE_URL="https://github.com/neo4j/apoc/releases/download/$NEO4J_LATEST_VERSION/apoc-$NEO4J_LATEST_VERSION-core.jar"
    echo "[$SCRIPT_NAME][INFO] - Downloading apoc-$NEO4J_LATEST_VERSION-core.jar."
    printf "\t$NEO4J_LATEST_APOC_CORE_URL\n"
    if [ $VERBOSE_MODE = true ]; then
        wget "$NEO4J_LATEST_APOC_CORE_URL"
    else
        wget -q "$NEO4J_LATEST_APOC_CORE_URL"
    fi
    ret_status=$?
    if [ $ret_status -eq 0 ]; then
        echo "[$SCRIPT_NAME][INFO] - Downloading apoc-$NEO4J_LATEST_VERSION-core.jar: success."
    else
        echo "[$SCRIPT_NAME][ERROR] - Downloading apoc-$NEO4J_LATEST_VERSION-core.jar: failed. Exiting."
        exit 1
    fi
    popd
else
     echo "[$SCRIPT_NAME][INFO] - No need to download apoc-$NEO4J_LATEST_VERSION-core.jar."
fi

# Download Neo4j APOC 'extended' JAR if necessary.
if [ ! -f "$PLUGINS_DIR/apoc-$NEO4J_LATEST_VERSION-extended.jar" ] || [ $DOWNLOAD_JAR_FORCED = true ] ; then
    pushd "$PLUGINS_DIR"
    NEO4J_LATEST_APOC_EXTENDED_URL="https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/download/$NEO4J_LATEST_VERSION/apoc-$NEO4J_LATEST_VERSION-extended.jar"
    echo "[$SCRIPT_NAME][INFO] - Downloading apoc-$NEO4J_LATEST_VERSION-extended.jar."
    printf "\t$NEO4J_LATEST_APOC_EXTENDED_URL\n"
    if [ $VERBOSE_MODE = true ]; then
        wget "$NEO4J_LATEST_APOC_EXTENDED_URL"
    else
        wget -q "$NEO4J_LATEST_APOC_EXTENDED_URL"
    fi
    ret_status=$?
    if [ $ret_status -eq 0 ]; then
        echo "[$SCRIPT_NAME][INFO] - Downloading apoc-$NEO4J_LATEST_VERSION-extended.jar: success."
    else
        echo "[$SCRIPT_NAME][ERROR] - Downloading apoc-$NEO4J_LATEST_VERSION-extended.jar: failed. Exiting."
        exit 1
    fi
    popd
else
     echo "[$SCRIPT_NAME][INFO] - No need to download apoc-$NEO4J_LATEST_VERSION-extended.jar."
fi

# NOTE: this comment block below remains here in case changing group is necessary for some reason.
# Helper: Docker permissions.
# https://stackoverflow.com/questions/51342810/how-to-fix-dial-unix-var-run-docker-sock-connect-permission-denied-when-gro
# https://stackoverflow.com/a/66079754/1708550
# Change group ID to docker.
# Based on: https://unix.stackexchange.com/a/18902
# /usr/bin/newgrp users <<EONG

# echo "NEW GROUP"
# newgrp docker
# ret_status=$?
# echo "NEW GROUP DONE"
# if [ $ret_status -eq 0 ]; then
#     echo "[$SCRIPT_NAME][INFO] - Changed to group 'docker'."
# else
#     echo "[$SCRIPT_NAME][ERROR] - Failed to change to group 'docker'."
#     popd
#     exit 1
# fi

# Build the Docker image.
PHYLODB_NAME=$(get_phylodb_name)
echo "[$SCRIPT_NAME][INFO] - Building Docker image '$PHYLODB_NAME'."

# We are checking if 'buildx' is available, and if not, defaulting to the 
# deprecated 'docker build' for backwards compatibility.
DOCKER_BUILD_COMMAND=$(get_docker_build_command)
echo "[$SCRIPT_NAME][INFO] - Docker build command: '$DOCKER_BUILD_COMMAND'."

DOCKERFILE_PATH="$PROJ_ROOT/docker/Dockerfile"

if [ $DOCKER_BUILD_COMMAND = 'buildx' ] ; then   
    if [ $VERBOSE_MODE = true ]; then
        docker $DOCKER_BUILD_COMMAND build --build-arg phylodb_version=$PHYLODB_VERSION -t $PHYLODB_NAME -f $DOCKERFILE_PATH .
    else
        docker $DOCKER_BUILD_COMMAND build --build-arg phylodb_version=$PHYLODB_VERSION --quiet -t $PHYLODB_NAME -f $DOCKERFILE_PATH .
    fi
else
    if [ $VERBOSE_MODE = true ]; then
        docker $DOCKER_BUILD_COMMAND  --build-arg phylodb_version=$PHYLODB_VERSION -t $PHYLODB_NAME -f $DOCKERFILE_PATH .
    else
        docker $DOCKER_BUILD_COMMAND --build-arg phylodb_version=$PHYLODB_VERSION --quiet -t $PHYLODB_NAME -f $DOCKERFILE_PATH .
    fi
fi

# Return to the initial directory when this script was called.
popd