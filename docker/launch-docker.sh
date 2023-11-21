#!/bin/bash

# Move into current script's directory.
PROJ_ROOT="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"
pushd "$PROJ_ROOT"  # cd current directory

newgrp docker

cd "instance1"
#USER=$(id -u):$(id -g) DB_PATH=$HOME/instance1/db APP_PATH=$HOME/instance1/app docker-compose up -d
USER=$(id -u):$(id -g) DB_PATH=$PROJ_ROOT/instance1/db APP_PATH=$PROJ_ROOT/instance1/app docker-compose up -d

# Create a session within the running Docker container.

#docker exec -ti phylodb-sourcegit_db_1 sh -c "cypher-shell -u neo4j -p password"

SCHEMA_QUERY_FILE="$PROJ_ROOT/scripts/init/init_schema.cypher"
#docker exec -ti phylodb-sourcegit_db_1 sh -c "cypher-shell -u neo4j -p password < $(cat $SCHEMA_QUERY_FILE)"

# Create admin user in Neo4j.
#TODO: run the .cypher file from this script using docker exec.

# Insert some data in Neo4j.
#TODO: run the .cypher file from this script using docker exec.


# Change back to original group ID.
newgrp

popd