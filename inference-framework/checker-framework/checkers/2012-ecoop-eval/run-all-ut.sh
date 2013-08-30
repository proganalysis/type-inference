#!/bin/bash
dir=ut-result-`date +%b%d-%H-%M`
mkdir -p $dir

dist=build
rm -r $dist
mkdir -p $dist

for bench in jolden tinysql htmlparser eclipse javad jbb jdepend classycle 
do
  echo "inferring pure methods for $bench"
  ./run-$bench-ri.sh 2>&1 > /dev/null
  echo "finished inferring pure methods for $bench"
  ./run-$bench-ut.sh 2>&1 | tee $dir/$bench-ut.log
  mv pure-methods.csv $dir/$bench-pure-methods.csv
  mv ri-result.csv $dir/$bench-ri-result.csv
  mv ut-result.csv $dir/$bench-ut-result.csv
  mv ut-fields-allocs.csv $dir/$bench-fields.csv
  total_time=`cat $dir/$bench-ut.log | grep '^real' | awk '{print $2}' | awk 'BEGIN {FS="[ms]";sum=0} {sum = $1*60 + $2} END{printf "%.1f\n", sum}'`
  echo "total running time: " $total_time
  cat $dir/$bench-ut.log | grep '<total>' | sed "s/<total>/$total_time/g" >> $dir/$bench-ut.log
done
echo "errors:"
grep '^error' $dir/*.log

echo "warning:"
grep ' warnning' $dir/*.log

echo "exceptions:"
grep  -l '^Caused.*Exception' $dir/*.log | uniq
