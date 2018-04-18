# Testing framework for Javarifier.
# Run 'make all' to perform the test and show the results.

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
# For the jolden case study, explicitly list all the classes that need to be
# analyzed for each program.
voronoi.jaif: ../../javarifier.jar
	cd voronoi && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries voronoi \
	--output $@ \
	--outputFormat jaif \
	Edge EdgePair MyDouble Vec2 Vertex Voronoi \
	2>&1 | tee voronoi.log

health.jaif: ../../javarifier.jar
	cd health && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries health \
	--output $@ \
	--outputFormat jaif \
	Health Hospital List Patient Results Village \
	2>&1 | tee health.log

mst.jaif: ../../javarifier.jar
	cd mst && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries mst \
	--output $@ \
	--outputFormat jaif \
	BlueReturn Graph HashEntry Hashtable MST Vertex \
	2>&1 | tee mst.log

perimeter.jaif: ../../javarifier.jar
	cd perimeter && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries perimeter \
	--output $@ \
	--outputFormat jaif \
	BlackNode GreyNode NorthEast NorthWest Perimeter QuadTreeNode Quadrant \
	SouthEast SouthWest WhiteNode \
	2>&1 | tee perimeter.log

em3d.jaif: ../../javarifier.jar
	cd em3d && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries em3d \
	--output $@ \
	--outputFormat jaif \
	BiGraph Em3d Graph Node \
	2>&1 | tee em3d.log

treeadd.jaif: ../../javarifier.jar
	cd treeadd && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries treeadd \
	--output $@ \
	--outputFormat jaif \
	TreeAdd TreeNode \
	2>&1 | tee treeadd.log

bh.jaif: ../../javarifier.jar
	cd bh && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries bh \
	--output $@ \
	--outputFormat jaif \
	BH Body Cell MathVector Node Tree \
	2>&1 | tee bh.log

tsp.jaif: ../../javarifier.jar
	cd tsp && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries tsp \
	--output $@ \
	--outputFormat jaif \
	TSP Tree \
	2>&1 | tee tsp.log

power.jaif: ../../javarifier.jar
	cd power && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries power \
	--output $@ \
	--outputFormat jaif \
	Branch Demand Lateral Leaf Power Root \
	2>&1 | tee power.log

bisort.jaif: ../../javarifier.jar
	cd bisort && javac -g *.java
	${JAVARIFIER} \
	--programCPEntries bisort \
	--output $@ \
	--outputFormat jaif \
	BiSort Value \
	2>&1 | tee bisort.log

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
