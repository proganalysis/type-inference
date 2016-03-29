#!/bin/bash

mydir="`dirname $0`"
dbfolder="/home/alex/DroidBench-iccta/dbfile"
export JAVA_HOME=$mydir/jre
export JAVA=$JAVA_HOME/bin/java

if [ $# != 1 ]; then 
  echo "Usage: $(basename $0) <dir-containing-apk>"
  $JAVA -version
  if ! [ $? -eq 0 ]; then
    echo "Have you installed Oracle Java 7? "
  fi
  exit 1
fi
apkdir=$1

if ! [ -d $apkdir ]; then
  echo "Cannot find directory $apkdir."
  exit 1
fi

apkfile=`find $apkdir/ -maxdepth 1 -mindepth 1 -name "*.apk"`
result=$apkdir/type_errors.txt
sflow_log=$apkdir/droidinfer.log
manifile=$apkdir/manifest.xml
echo `pwd`

echo "archiving intent filters"
rm $dbfolder/*
$JAVA -jar $mydir/APKParser.jar $apkfile > $manifile
$JAVA -cp $mydir/soot-develop.jar soot.intentResolve.intentFilterArchive  $manifile

echo "Analyzing $apkfile..."
echo "Logging the output to $sflow_log..."
date1=$(date +"%s")
$mydir/soot-sflow -DpreferSource $apkfile -f J -d $apkdir 2>&1 > $sflow_log

if ! [ $? -eq 0 ]; then
  echo "Something went wrong! Please check the output at $sflow_log"
  exit 1
fi

mkdir -p $apkdir/jimpleFiles
mv $apkdir/*.jimple $apkdir/jimpleFiles
cat $sflow_log | grep '^SUB-\|^EQU-' > $result
echo "Saved type error result to $result"

echo "Generating call graphs..."

cgresult=$apkdir/call_graphs.txt

# huangw5: A hacky solution to run wala.
cur_dir=$(pwd)
cd $mydir
$JAVA -Xmx1400m -jar ./cgs.jar $cur_dir/$apkfile 400000  2>&1 | grep "^L" > $cur_dir/$cgresult 
if ! [ $? -eq 0 ]; then
  echo "Something went wrong! Please check the output at $cur_dir/$cgresult"
  exit 1
fi
cd $cur_dir
echo "Saved call graph result to $cgresult"

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "Total running time: $(($diff / 60)) minutes and $(($diff % 60)) seconds"
