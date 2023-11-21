#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
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
    echo "-s     Skip JAR file building in case it already exists."
    echo "-v     Verbose mode."
    echo "-h     Print this Help."
    echo
}

# Process arguments.
CHECK_VERSIONS=false
SKIP_REBUILD_JARS=false
VERBOSE_MODE=false
while getopts :csvh flag
do
    case "${flag}" in
        c) CHECK_VERSIONS=true;;
        s) SKIP_REBUILD_JARS=true;;
        v) VERBOSE_MODE=true;;
        h) #display Help
            Help
            exit;;
    esac
done

source "$SCRIPT_DIR/../util.sh"
if [ $CHECK_VERSIONS = true ]; then
    check_java
    check_gradle
fi

PHYLODB_NAME=$(get_phylodb_name)
PHYLODB_VERSION=$(get_phylodb_version)
PHYLODB_DIR="$SCRIPT_DIR"
PHYLODB_JAR_BASE_NAME="$PHYLODB_NAME-$PHYLODB_VERSION.jar"
PHYLODB_JAR="$PHYLODB_DIR/build/libs/$PHYLODB_JAR_BASE_NAME"

if [ ! -f "$PHYLODB_JAR" ] || [ $SKIP_REBUILD_JARS = false ] ; then
    echo "[$SCRIPT_NAME][INFO] - Building $PHYLODB_NAME: $PHYLODB_JAR."
    pushd "$PHYLODB_DIR"
    if [ $VERBOSE_MODE = true ]; then
        gradle bootJar -i
    else
        gradle bootJar -i > "$PHYLODB_DIR/build.log" 2>&1
    fi
    popd
else
    echo "[$SCRIPT_NAME][INFO] - Found $PHYLODB_JAR, skipping build."
fi