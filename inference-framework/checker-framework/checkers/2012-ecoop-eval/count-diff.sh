#!/bin/bash

#otdir="ot-result-Dec17-22-31"
#utdir="ot-result-Dec17-22-31"

utdir="ut-result-Dec17-12-01"
otdir="ot-result-Dec17-11-26"

bench="jdepend"

for bench in jolden javad jbb jdepend
do

echo "-----$bench------"

# rep/rep
cat $otdir/$bench-fields.csv | grep '\.java'  | grep '\<REP\>' | awk '{print $1$2$3}' > ot-rep
cat $utdir/$bench-fields.csv | grep '\.java'  | grep '\<REP\>' | awk '{print $1$2$3}' > ut-rep
total=`cat ot-rep ut-rep | wc | awk '{print $1}'`
unique=`cat ot-rep ut-rep | sort | uniq | wc | awk '{print $1}'`
echo "total ot rep: " `cat ot-rep | wc | awk '{print $1}'`
echo "rep/rep: " `expr $total - $unique`

# rep/any
cat $utdir/$bench-fields.csv | grep '\.java'  | grep '\<ANY\>' | awk '{print $1$2$3}' > ut-any
total=`cat ot-rep ut-any | wc | awk '{print $1}'`
unique=`cat ot-rep ut-any | sort | uniq | wc | awk '{print $1}'`
echo "rep/any: " `expr $total - $unique`

# rep/peer
cat $utdir/$bench-fields.csv | grep '\.java'  | grep '\<PEER\>' | awk '{print $1$2$3}' > ut-peer
total=`cat ot-rep ut-peer | wc | awk '{print $1}'`
unique=`cat ot-rep ut-peer | sort | uniq | wc | awk '{print $1}'`
echo "rep/peer: " `expr $total - $unique`

# ~rep/rep
cat $otdir/$bench-fields.csv | grep '\.java'  | grep -v '\<REP\>' | awk '{print $1$2$3}' > ot-nonrep
echo "total ot nonrep: " `cat ot-nonrep | wc | awk '{print $1}'`
total=`cat ot-nonrep ut-rep | wc | awk '{print $1}'`
unique=`cat ot-nonrep ut-rep | sort | uniq | wc | awk '{print $1}'`
echo "~rep/rep: " `expr $total - $unique`

# ~rep/~rep
cat $utdir/$bench-fields.csv | grep '\.java'  | grep -v '\<REP\>' | awk '{print $1$2$3}' > ut-nonrep
total=`cat ot-nonrep ut-nonrep | wc | awk '{print $1}'`
unique=`cat ot-nonrep ut-nonrep | sort | uniq | wc | awk '{print $1}'`
echo "~rep/~rep: " `expr $total - $unique`

#rm ot-rep
#rm ot-nonrep
#rm ut-rep
#rm ut-peer
#rm ut-any
#rm ut-nonrep

echo 
done
