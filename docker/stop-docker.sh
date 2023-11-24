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

PROJ_ROOT="$SCRIPT_DIR/.."

pushd "$PROJ_ROOT"

PHYLODB_DB_CONTAINER_NAME="docker-db-1"
echo "[$SCRIPT_NAME][INFO] - Stopping 'phylodb' image container $PHYLODB_DB_CONTAINER_NAME"
docker container stop $PHYLODB_DB_CONTAINER_NAME

PHYLODB_APP_CONTAINER_NAME="docker-app-1"
echo "[$SCRIPT_NAME][INFO] - Stopping 'neo4j:5.13.0' image container $PHYLODB_APP_CONTAINER_NAME"
docker container stop $PHYLODB_APP_CONTAINER_NAME

popd
