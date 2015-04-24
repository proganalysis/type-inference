#!/bin/bash

files=`find src -name '*.java'`

jarFiles=`find lib -name '*.jar' | xargs | sed 's/ /:/g'`

jcryptDir=../type-inference/trunk/inference-framework/checker-framework/checkers

exec $jcryptDir/binary/javai-jcrypt2 $files -classpath $jarFiles
