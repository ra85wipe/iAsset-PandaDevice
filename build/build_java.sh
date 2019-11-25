#!/bin/sh

##
#MVN="mvn -ntp -Duser.home=/home/jenkins/agent"

CWD=$(pwd)
echo "CWD: $CWD"

#GIT_DIFF=$(/usr/bin/git diff --name-only origin/master)
#JAVA_SDK_CHANGED=$(echo $GIT_DIFF | grep ".*/sdks/java/.*" | wc -l)


#if [ $((JAVA_SDK_CHANGED > 0)) ];
#then
    cd ../sdks/java/basys.sdk
    mvn clean install
    cd "$CWD"

    cd ../components/basys.components
    mvn clean install
    cd "$CWD"

    cd ../panda/basys.panda
    mvn verify
    cd "$CWD"
#fi
