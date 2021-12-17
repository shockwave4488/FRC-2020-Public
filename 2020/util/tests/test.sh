#!/bin/bash

expected=""
actual=$(git diff)

if [[ $actual -eq $expected ]]
then
    echo "Clean working Directory"
else
    echo "Dirty workspace"
    exit -1
fi

wget -v https://github.com/google/google-java-format/releases/download/google-java-format-1.5/google-java-format-1.5-all-deps.jar
find . -name "*.java" -exec java -jar google-java-format-1.5-all-deps.jar -i {} \;

expected=""
actual=$(git diff)

if [[ $actual -eq $expected ]]
then
    echo "passed"
else
    echo "Failed"
    exit -1
fi
