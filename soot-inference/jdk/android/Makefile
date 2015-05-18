## This file, checkers/scripts/Makefile.jdk, is linked from
## each subdirectory of checkers/jdk/ .  For example,
## checkers/jdk/nullness/Makefile is a symbolic link to this file.


# Variables
CHECKERS = ../../
CHECKERS_JAR = $(CHECKERS)/lib/soot-inference.jar
ANDROID_JAR = android-all.jar

JAVAC = javac -Xlint

#JAVA_HOME = /projects/proganalysis/Software/jdk1.6.0_27/
RTJAR = $(JAVA_HOME)/jre/lib/rt.jar

ANNOTATION_TOOLS = ../../../../annotation-tools
AFU_SCRIPTS = $(ANNOTATION_TOOLS)/annotation-file-utilities/scripts

all: clean classes
# Separate make command because BUILD_CLASSES needs to be recomputed.
#   $(MAKE) jaifs annotated

clean:
	rm -rf build
	rm -rf annotated 

build: classes


############## Validate annotated jdk against missing methods ##############

SOURCES = $(shell find src/ -name '*.java')

.PHONY: classes annotated
classes: $(SOURCES)
	mkdir -p build
	@echo "Building the JDK..."
	@$(JAVAC) -cp $(CHECKERS_JAR):$(ANDROID_JAR) -d build $?
	mkdir -p annotated
	cd annotated; unzip -o $(RTJAR) > /dev/null
	cd annotated; unzip -o ../$(ANDROID_JAR) > /dev/null
	cp -r build/* annotated
	cd annotated; jar cf ../jdk-android.jar .
	cp jdk-android.jar $(CHECKERS)/binary/

############## Annotate the annotated jdk file ##############

# Problem: build/java doesn't exist until after the "classes" target has
# run!  This means that a new make process must be started, after the
# "classes" rule has executed, before any rule that uses these variables.
# Furthermore, these shell commands may issue errors saying that build/java
# does not exist.  So long as the variables are not used on such a make
# invocation, this doesn't cause a problem, but it is irritating (and a bit
# sloppy).
BUILD_CLASSES = $(shell find build -name '*.class')
JAIFS = $(patsubst build/%.class,build/%.jaif, $(BUILD_CLASSES))
ANNOTATED = $(patsubst build/%.class,annotated/%.class, $(BUILD_CLASSES))

build/%.jaif: build/%.class
	$(AFU_SCRIPTS)/extract-annotations '$<'

jaifs: $(JAIFS)

annotated/%.class: build/%.jaif
	$(AFU_SCRIPTS)/insert-annotations '$@' '$<'

annotated: 
	cd annotated; unzip -o ../$(ANDROID_JAR) > /dev/null
	$(MAKE) $(ANNOTATED)
	cd annotated; jar cf ../jdk-android.jar .

showvars:
	pwd
	@echo BUILD_CLASSES: ${BUILD_CLASSES}
	@echo JAIFS: ${JAIFS}
	@echo ANNOTATED: ${ANNOTATED}
