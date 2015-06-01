#!/bin/bash

cfltoolpath=~/cfl-reachability/
#path of the type error tool output
typeErrorPath=/projects/proganalysis/TaintAnalysis/soot-inference/android-tests/GooglePlay/checked/

outputFile=pathResults.txt
cd $typeErrorPath

if [ ! -e "applist.txt" ]; then
	ls -d *>"applist.txt"
fi

applist=`cat applist.txt`

for apkfolder in $applist; do
	if [[ -d $apkfolder ]]; then
	    echo $apkfolder
		echo $apkfolder>>pathResults.txt
    	txtfile=$apkfolder".apk.txt"
		line=`awk '/Total restor/{print NR-1}' $apkfolder/$txtfile |sed -n 1p`
		sources=`tail -n +$line $apkfolder/$txtfile|grep SUB|grep -Po '(?<==m=>\ \()[0-9]*'`
		if  [ "$sources" != "" ]; then
			for sourcenode in $sources; do
				echo "	Source:"$sourcenode>>pathResults.txt
				java -cp $cfltoolpath Graph $apkfolder/sflow-constraints.log>>pathResults.txt
			done
		fi
		echo "==================="
		sed "/$apkfolder/d" applist.txt
#		if [ "$empty" != "" ]; then
#			break
#		fi
		sed "/$apkfolder/d" applist.txt > applist.txt
	    sleep 3
	fi
done
