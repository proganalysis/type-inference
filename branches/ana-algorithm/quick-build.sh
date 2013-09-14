#!/bin/bash
svn st ./src/ | grep --color=never '\.java' | awk '{print $2}' | xargs  ../../jsr308-langtools/dist/bin/javac -g -cp ./build/:./lib/javaparser.jar:./lib/jna.jar -d ./build/
