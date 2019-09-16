#!/bin/sh -ve
DIR=$(dirname "$(readlink -f "$0")")
export TAG=$CIRCLE_SHA1
. $DIR/transform_vars.sh
mkdir -p /tmp/workspace/

# Test gradle build
./gradlew test

### Jib build
# ./gradlew jibBuildTar
# mv build/jib-image.tar /tmp/workspace

### Default - shadowJar build
docker build -t zensum/$ZENS_NAME:latest -t zensum/$ZENS_NAME:$TAG --build-arg JITPACK_TOKEN .
docker save -o /tmp/workspace/latest zensum/$ZENS_NAME:$TAG
