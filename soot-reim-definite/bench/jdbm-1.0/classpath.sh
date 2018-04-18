#!/bin/bash

ROOT=`pwd`

jars=`find . -name '*.jar' | awk -v parent=$ROOT '{printf "%s/%s\n", parent, $1}' | xargs`
jars=`echo $jars | sed -e 's/ /:/g'`
echo $ROOT/build/classes:$jars
