#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build basyx project for java sdk
cd ../iAsset-Basyx/sdks/java/basys.sdk
mvn clean install -DskipTests
cd "$CWD"

# build basyx project for components
cd ../iAsset-Basyx/components/basys.components
mvn clean install -DskipTests
cd "$CWD"

# build basyx project for examples (not needed)
cd ../iAsset-Basyx/examples/basys.examples
mvn clean install -DskipTests
cd "$CWD"

# build panda project of iAsset
cd ../panda/basys.panda
mvn clean install
mvn verify
cd "$CWD"

