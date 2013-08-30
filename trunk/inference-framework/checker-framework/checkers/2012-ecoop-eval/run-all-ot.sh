#!/bin/bash
dir=ot-result-`date +%b%d-%H-%M`
mkdir -p $dir

dist=build
rm -r $dist
mkdir -p $dist

for bench in jolden tinysql htmlparser eclipse javad jbb jdepend classycle
do
  ./run-$bench-ot.sh 2>&1 | tee $dir/$bench-ot.log
  mv ot-result.csv $dir/$bench-result.csv
  mv ot-fields-allocs.csv $dir/$bench-fields.csv
  mv result.jaif $dir/$bench-result.jaif
  total_time=`cat $dir/$bench-ot.log | grep '^real' | awk '{print $2}' | awk 'BEGIN {FS="[ms]";sum=0} {sum = $1*60 + $2} END{printf "%.1f\n", sum}'`
  echo "total running time: " $total_time
  cat $dir/$bench-ot.log | grep '<total>' | sed "s/<total>/$total_time/g" >> $dir/$bench-ot.log
done
echo "errors:"
grep 'error' $dir/*.log

echo "exceptions:"
grep  -l '^Caused.*Exception' $dir/*.log | uniq
