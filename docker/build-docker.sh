#!/bin/bash

# Move into current script's directory.
PROJ_ROOT="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"
pushd "$PROJ_ROOT"  # cd current directory

# Built the solution JAR files if necessary.
echo ""
echo "#####"
echo "##### Checking algorithms-1.0.jar..."
echo "#####"
echo ""

if [ ! -f "$PROJ_ROOT/algorithms/target/algorithms-1.0.jar" ] ; then
    cd "algorithms"
    mvn package -Dmaven.test.skip=true
    cd "$PROJ_ROOT"
else
    echo "> Found: algorithms-1.0.jar, skipping build."
fi

echo ""
echo ""


echo ""
echo "#####"
echo "##### Checking phylodb-1.0.jar..."
echo "#####"
echo ""

if [ ! -f "$PROJ_ROOT/phylodb/build/libs/phylodb-1.0.0.jar" ] ; then
    cd "phylodb"
    gradle bootJar
    cd "$PROJ_ROOT"
else
    echo "> Found: algorithms-1.0.jar, skipping build."
fi

echo ""
echo ""

echo ""
echo "#####"
echo "##### Checking Docker files..."
echo "#####"
echo ""

# Create Docker drive structure.
INSTANCE_1_DIR="$PROJ_ROOT/instance1"
PLUGINS_DIR="$INSTANCE_1_DIR/db/plugins"

mkdir -p "$INSTANCE_1_DIR/db/data"
mkdir -p "$INSTANCE_1_DIR/db/logs"
mkdir -p "$PLUGINS_DIR"
mkdir -p "$INSTANCE_1_DIR/app/logs"

# Copy the algorithms JAR file.
if [ ! -f "$PLUGINS_DIR/algorithms-1.0.jar" ] ; then
    echo ""
    echo "> Copying algorithms-1.0.jar."
    cp "$PROJ_ROOT/algorithms/target/algorithms-1.0.jar" "$PLUGINS_DIR/"
    echo ""
fi



# Get Neo4j's APOC JAR files.
if [ ! -f "$PLUGINS_DIR/apoc-4.4.0.5-all.jar" ] ; then
    echo ""
    echo "> Dowloading apoc-4.4.0.5-all.jar."
    cd "$PLUGINS_DIR"
    wget "https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/download/4.4.0.5/apoc-4.4.0.5-all.jar"
    cd "$PROJ_ROOT"
    echo ""
fi

echo "> Done."

# Build the Docker image.
echo ""
echo ""

echo ""
echo "#####"
echo "##### Checking the Docker image..."
echo "#####"
echo ""

# Helper: Docker permissions.
# https://stackoverflow.com/questions/51342810/how-to-fix-dial-unix-var-run-docker-sock-connect-permission-denied-when-gro
# https://stackoverflow.com/a/66079754/1708550

cd "$PROJ_ROOT/phylodb"

# Change group ID to docker.
newgrp docker
docker build -t phylodb .

# Change back to original group ID.
newgrp
cd ..

# Return to the initial directory.
popd