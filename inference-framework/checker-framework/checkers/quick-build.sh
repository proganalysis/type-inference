#!/bin/bash
svn st ./src/ | awk '{print $2}' | xargs  ../../jsr308-langtools/dist/bin/javac -g -cp ./build/:./lib/javaparser.jar:./lib/jna.jar -d ./build/
