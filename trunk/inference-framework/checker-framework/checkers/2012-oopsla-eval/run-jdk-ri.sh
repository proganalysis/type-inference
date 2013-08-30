#!/bin/bash

benchdir=../benchmarks
time find $benchdir/jdk-src-1.6/java/ -name '*.java' | grep -v 'awt\|beans' | xargs ../binary/javai-reim -d build/ -cp /cs/huangw5/Softwares/jdk1.6.0_27/jre/lib  
