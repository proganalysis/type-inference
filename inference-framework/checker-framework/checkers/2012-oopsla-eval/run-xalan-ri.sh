#!/bin/bash

benchdir=../benchmarks
dir=$benchdir/xalan-j_2_7_1

myclasspath=`find $dir/lib $dir/tools -name '*.jar' | xargs | sed -e 's/ /:/g'`

files=`find $dir/src -name '*.java'`

#exec ../binary/jdb-ri -cp $myclasspath $files
time ../binary/javai-reim -AlibPureMethods=lib-pure-methods.csv -AlibMutateStatics=lib-mutate-statics.csv -cp $myclasspath $files


