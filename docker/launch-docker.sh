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

INSTANCE_DIR="$SCRIPT_DIR/instance"
pushd "$INSTANCE_DIR"

APP_CONTAINER_IS_RUNNING=$(docker-compose exec app echo "true")
if [ -z "$APP_CONTAINER_IS_RUNNING" ]
then
      APP_CONTAINER_IS_RUNNING=false
else
      APP_CONTAINER_IS_RUNNING=true
fi

DB_CONTAINER_IS_RUNNING=$(docker-compose exec db echo "true")
if [ -z "$DB_CONTAINER_IS_RUNNING" ]
then
      DB_CONTAINER_IS_RUNNING=false
else
      DB_CONTAINER_IS_RUNNING=true
fi

NEO4J_LATEST_VERSION=$(get_latest_stable_neo4j_version)



if [ $APP_CONTAINER_IS_RUNNING = false ] || [ $DB_CONTAINER_IS_RUNNING = false ] ; then
    echo "[$SCRIPT_NAME][INFO] - Launching 'phylodb' and 'db' containers with 'docker-compose'."
    NEO4J_VERSION="$NEO4J_LATEST_VERSION" USER=$(id -u):$(id -g) DB_PATH=$PROJ_ROOT/docker/instance/db APP_PATH=$PROJ_ROOT/docker/instance/app docker-compose up -d
    ret_status=$?
    if [ $ret_status -eq 0 ]; then
        echo "[$SCRIPT_NAME][INFO] - 'phylodb' and 'db' containers started."
    else
        echo "[$SCRIPT_NAME][ERROR] - 'phylodb' and 'db' container start failed. Exiting."
        exit 1
    fi
else
    echo "[$SCRIPT_NAME][INFO] - 'phylodb' and 'db' containers already running."
fi

# Passing file contents to Neo4j container cypher via 'cat':
# https://neo4j.com/docs/operations-manual/current/docker/operations/

# Initialize Neo4j PhyloDB schema (creates indices and properties).
SCHEMA_QUERY_FILE="$PROJ_ROOT/scripts/init/init_schema.cypher"
cat $SCHEMA_QUERY_FILE | docker exec --interactive docker_db_1 sh -c "cypher-shell -u neo4j -p password" >/dev/null 2>&1

# Initialize Neo4j PhyloDB data.
DATA_QUERY_FILE="$PROJ_ROOT/scripts/init/init_data.cypher"
cat $DATA_QUERY_FILE | docker exec --interactive docker_db_1 sh -c "cypher-shell -u neo4j -p password" >/dev/null 2>&1

# Create admin user in Neo4j.
#TODO: run the .cypher file from this script using docker exec.

# Insert some data in Neo4j.
#TODO: run the .cypher file from this script using docker exec.


popd