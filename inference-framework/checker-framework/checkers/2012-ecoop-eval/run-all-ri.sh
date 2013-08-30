#!/bin/bash
dir=ri-result-`date +%b%d-%H-%M`
dist=build
mkdir -p $dir

rm -r $dist
mkdir -p $dist

for bench in jolden tinysql htmlparser eclipse xalan javad jbb \
  commons-pool jdbm jdbf jtds java-lang java-util 
do
  ./run-$bench-ri.sh 2>&1 | tee $dir/$bench-ri.log
  mv new-pure-methods.csv $dir/$bench-pure-methods.csv
  mv new-mutatestatics.csv $dir/$bench-mutatestatics.csv
  mv new-result.csv $dir/$bench-result.csv
  mv new-result.jaif $dir/$bench-result.jaif
  total_time=`cat $dir/$bench-ri.log | grep real | awk '{print $2}' | awk 'BEGIN {FS="[ms]";sum=0} {sum = $1*60 + $2} END{printf "%.1f\n", sum}'`
  infer_time=`cat $dir/$bench-ri.log | grep inferrence_time | awk '{print $2}'`
  checking_time=`cat $dir/$bench-ri.log | grep checking_time | awk '{print $2}'`
  echo "total running time: " $total_time
  echo "inferrence time: " $infer_time
  echo "checking time: " $checking_time
  cat $dir/$bench-ri.log | grep '<total>' | sed "s/<total>/$total_time/g" | sed "s/<inferTime>/$infer_time/g" | sed "s/<checkingTime>/$checking_time/g"
  cat $dir/$bench-ri.log | grep '<total>' | sed "s/<total>/$total_time/g" | sed "s/<inferTime>/$infer_time/g" | sed "s/<checkingTime>/$checking_time/g" >> $dir/$bench-ri.log
done
echo "errors:"
grep 'error: ' $dir/*.log

echo "warning:"
grep ' warnning' $dir/*.log

echo "exceptions:"
grep  -l '^Caused.*Exception' $dir/*.log | uniq
