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
    echo "-v     Verbose mode."
    echo "-h     Print this Help."
    echo
}

# Process arguments.
CHECK_VERSIONS=false
VERBOSE_MODE=false
while getopts :cvh flag
do
    case "${flag}" in
        c) CHECK_VERSIONS=true;;
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

ALGORITHMS_POM_NEO4J_VERSION=$(get_algorithms_neo4j_version)

NEO4J_LATEST_VERSION="$ALGORITHMS_POM_NEO4J_VERSION"

#NEO4J_LATEST_VERSION=$(get_latest_stable_neo4j_version)


PHYLODB_NAME=$(get_phylodb_name)

PROJ_ROOT="$SCRIPT_DIR/.."

pushd "$PROJ_ROOT"


#PHYLODB_DB_CONTAINER_NAME="$PHYLODB_NAME:latest"
PHYLODB_DB_CONTAINER_NAME="phylodb-neo4j"
echo "[$SCRIPT_NAME][INFO] - Stopping '$PHYLODB_NAME' image container $PHYLODB_DB_CONTAINER_NAME"
docker container stop $PHYLODB_DB_CONTAINER_NAME

#PHYLODB_APP_CONTAINER_NAME="neo4j:$NEO4J_LATEST_VERSION-community"
PHYLODB_APP_CONTAINER_NAME="phylodb-app"
echo "[$SCRIPT_NAME][INFO] - Stopping 'neo4j:$NEO4J_LATEST_VERSION' image container $PHYLODB_APP_CONTAINER_NAME"
docker container stop $PHYLODB_APP_CONTAINER_NAME

popd
