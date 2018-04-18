#!/bin/bash

basedir=.

baselen=`expr length $basedir`
baselen=`expr $baselen + 2`

classes=`find $basedir -type f -name '*.class' \
  | grep -v 'tests\|$1'  \
  | awk -v len=$baselen '{print substr($0, len)}' \
  | xargs \
  | sed -e "s/\.class//g" \
  | sed -e "s/\//\./g"`

echo $classes

javarifier  \
  --programCPEntries . \
  --stubs /cs/huangw5/Projects/proganalysis/projects/checker/checker-framework-1.1.5/checkers/jdk/jdk-javari.jar \
  --useWorldAsStubs \
  --output javad.jaif \
  --outputFormat jaif \
  $classes
