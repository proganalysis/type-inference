#!/usr/bin/env sh
#
# author: Quentin Sabah
#

deploy_dir=$PWD

jasmin_repo=http://github.com/Sable/jasmin.git
jasmin_branch=develop
jasmin_dir=$deploy_dir/jasmin-github

heros_repo=http://github.com/Sable/heros.git
heros_branch=develop
heros_dir=$deploy_dir/heros-github

soot_repo=http://github.com/Sable/soot.git
soot_branch=develop
soot_dir=$deploy_dir/soot-github

entry_dir=$PWD

script_fail() {
  echo "=== FAILED ==="
  echo "during operation: " $@
  exit 1
}

## JASMIN
if [ -d $jasmin_dir ]; then
  cd $jasmin_dir
  git checkout $jasmin_branch || script_fail "checkout jasmin"
  git pull || script_fail "pull jasmin"
else
  git clone $jasmin_repo -b $jasmin_branch $jasmin_dir || script_fail "clone jasmin"
fi
cd $jasmin_dir
echo 'java_cup.jar=libs/java_cup.jar' > ant.settings
echo 'release.loc=lib' >> ant.settings
echo "jasmin.version=$jasmin_branch" >> ant.settings
ant clean jasmin-jar || script_fail "build jasmin"

## HEROS
if [ -d $heros_dir ]; then
  cd $heros_dir
  git checkout $heros_branch || script_fail "checkout heros"
  git pull || script_fail "pull heros"
else
  git clone $heros_repo -b $heros_branch $heros_dir || script_fail "clone heros"
fi
cd $heros_dir
echo "heros.version=$heros_branch" > ant.settings
echo 'guava.jar=guava-13.0.1.jar' >> ant.settings
echo 'slf4j-api.jar=slf4j-api.jar' >> ant.settings
echo 'slf4j-simple.jar=slf4j-simple.jar' >> ant.settings
ant jar || script_fail "build heros"

## SOOT
if [ -d $soot_dir ]; then
  cd $soot_dir
  git checkout $soot_branch || script_fail "checkout soot"
  git pull || script_fail "pull soot"
else
  git clone $soot_repo -b $soot_branch $soot_dir || script_fail "clone soot"
fi
cd $soot_dir
echo 'xmlprinter.jar=libs/AXMLPrinter2.jar' > ant.settings
echo 'polyglot.jar=libs/polyglot.jar' >> ant.settings
echo 'baksmali.jar=libs/baksmali-1.3.2.jar' >> ant.settings
echo 'baksmali2.jar=libs/baksmali-2.0b5.jar' >> ant.settings
echo "jasmin.jar=$jasmin_dir/lib/jasminclasses-$jasmin_branch.jar" >> ant.settings
echo "heros.jar=$heros_dir/heros-$heros_branch.jar" >> ant.settings
echo "soot.version=$soot_branch" >> ant.settings
echo 'release.loc=lib' >> ant.settings
echo 'javaapi.url=http://docs.oracle.com/javase/6/docs/api/' >> ant.settings
echo 'junit.jar=libs/junit-4.10.jar' >> ant.settings
echo "javacup.jar=$jasmin_dir/libs/java_cup.jar" >> ant.settings
ant clean fulljar || script_fail "build soot"

echo "========================================================================"
echo "=== SUCCESS ==="
echo "Soot jar is in $soot_dir/lib/soot-develop.jar"

cd $entry_dir
