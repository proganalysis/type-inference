# Testing framework for Javarifier.
# Run 'make all' to perform the test and show the results.

TESTNAME = tinySQL
MAINCLASS = ORG.as220.tinySQL.tinySQL

SHELL = /bin/sh

DIFFS := $(patsubst %.goal, %.diff, $(wildcard *.goal))

JAVARIFIER ?= ../../scripts/javarifier

default : all

.PHONY: all
all : $(DIFFS) results

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
	--useWorldAsStubs \
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
