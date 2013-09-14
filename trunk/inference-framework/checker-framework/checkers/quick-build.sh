#!/bin/bash
files=`svn st ./src/ | grep --color=never '\.java' | awk '{print $2}'`
echo -n "building..."
echo $files
echo $files | xargs  ../../jsr308-langtools/dist/bin/javac -g -cp ./build/:./lib/javaparser.jar:./lib/jna.jar -d ./build/
