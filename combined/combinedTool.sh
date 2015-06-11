#!/bin/bash

cfltoolpath=~/soot-inference/workspace/type-inference/soot-inference/cfl-reachability/non_inter/
#path of the type error tool output
#typeErrorPath=/projects/proganalysis/TaintAnalysis/soot-inference/android-tests/GooglePlay/checked/
typeErrorPath=/projects/proganalysis/TaintAnalysis/soot-inference/android-tests/DroidBench

outputFile=pathResults.txt
statFile=errorStats.txt
cd $typeErrorPath
pwd

if [ ! -e "applist.txt" ]; then
	ls -d *>"applist.txt"
fi

applist=`cat applist.txt`
sourceCount=0
trueSourceCount=0
appCount=0
leakyAppCount=0

for apkfolder in $applist; do
	if [ -d $apkfolder ]; then
	    echo $apkfolder
		echo $apkfolder>>$outputFile
    	txtfile=$apkfolder".apk.txt"
		line=`awk '/Total restor/{print NR-1}' './'$apkfolder/'type_errors.txt' |sed -n 1p`
		sources=`tail -n +$line $apkfolder/'type_errors.txt'|grep SUB|grep -Po '(?<==m=>\ \()[0-9]*'`
		echo $sources
		hasTrueLeak=0
		uniquesources=`echo "$sources"|tr ' ' '\n'|sort -u`
		echo $uniquesources
		if  [ "$uniquesources" != "" ]; then
			for sourcenode in $uniquesources; do
				echo "	Source:"$sourcenode>>$outputFile
				java -cp $cfltoolpath Graph $apkfolder $sourcenode>tempFile.txt
				pathcount=`cat tempFile.txt|grep -Po '(?<=number\ of\ paths\ found:)[0-9]*'`
				if test $pathcount -gt 0
				then
					trueSourceCount=$((trueSourceCount+1))
					hasTrueLeak=1
				fi
				sourceCount=$((sourceCount+1))
				cat tempFile.txt>>$outputFile
			done
		fi
		appCount=$((appCount+1))
		leakyAppCount=$((leakyAppCount+hasTrueLeak))

		echo "souces: $sourceCount /true sources: $trueSourceCount  apps: $appCount /leakyapps: $leakyAppCount">>$statFile
		echo >>$outputFile
		echo "==================="
#		if [ "$empty" != "" ]; then
#			break
#		fi
		sed "/$apkfolder/d" applist.txt > newapplist.txt
		rm applist.txt
		mv newapplist.txt applist.txt
	    sleep 3
	fi
done
