# Testing framework for Javarifier.
# Run 'make all' to perform the test and show the results.

TESTNAME = eclipsec
MAINCLASS = org.eclipse.jdt.internal.compiler.Compiler

SHELL = /bin/sh

DIFFS := $(patsubst %.goal, %.diff, $(wildcard *.goal))

JAVARIFIER ?= javarifier

default : all

.PHONY: all
all : $(DIFFS) results

# No need to compile because the .class files are checked in (!).
# This should probably be corrected.  But, if I do this, then tests fail.
# (Perhaps because Javarifier and/or Soot can't deal with new class file format?)
compile:
	javac -g `find . -name "*.java" -print`

# Display results of all .diff files.
.PHONY: results
results: ../VerifyDiffs.class
	@echo ""
	@echo "=== RESULTS ==="
	@echo ""
	@-java -cp .. VerifyDiffs --show_all

# Remakes the little java program that checks and compares diffs
../VerifyDiffs.class : ../VerifyDiffs.java
	${MAKE} -C .. VerifyDiffs.class

# Actually runs the Javarifier to create the inferences.
${TESTNAME}.jaif: ../../javarifier.jar
	${JAVARIFIER} \
	--programCPEntries . \
	--output $@ \
	--outputFormat jaif \
	${MAINCLASS} \
	2>&1 | tee ${TESTNAME}.log


# Compare the output of the Javarifier and the goal file.
%.diff: %.goal %.jaif
	-diff -u $*.goal $*.jaif > $*.diff 2>&1

# Remove all .diff, .log files from the tests directory,
# and all output from the javarifierOutput directory.
.PHONY: clean
clean :
	rm -f *.diff
	rm -f *.log
	rm -f *.jaif
