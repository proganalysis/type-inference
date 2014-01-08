#!/bin/bash

CLASS=$1
#FILE="src/"`echo $CLASS | sed "s/\./\//g"`".java"
#PACKAGE=`echo $CLASS | sed 's/\.[0-9a-zA-Z_]\+$//g'`

#echo "package $PACKAGE" 

#java -Xbootclasspath/p:../../binary/jsr308-all.jar -cp android-all.jar  checkers.util.stub.StubGenerator $1 | sed '/^[^()]\+;$/d' | sed 's/);$/) { throw new RuntimeException("skeleton method"); }/g' | sed 's/^class/public class/' 
java -Xbootclasspath/p:../../binary/jsr308-all.jar -cp android-4.3.jar  checkers.util.stub.StubGenerator $1 
