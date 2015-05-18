# type-inference
Automatically exported from code.google.com/p/type-inference

Note: Inference results for detecting information violations in Android apps are available: androidapps-results.tgz. The instantiated inference will be released shortly (available in the source repository).

Note: Inference results for detecting information violations in web apps are available: webapps-results.tgz. The instantiated inference will be released shortly (available in the source repository).

Introduction
Extends the Checker Framework
Currently supports inference of Universe Types, Ownership Types, Reference Immutability(reim) and EnerJ.
Download: type-inference-0.1.2.zip contains executable binary and source code. NOTE: It contains Reference Immutability(reim), Ownership Types, and Universe Types in this zip file. Others will be added soon.
Installation
The following instructions assume an Unix-like (e.g Mac, Linux) environment.

Requirement: You must have JDK 6 or later installed. The binary release was compiled and tested under JDK 6 on Mac OS X 10.7 and Ubuntu 12.04.

Download type-inference-0.1.2.zip and unzip it to create a type-inference directory
Optional Add type-inference/binary to your PATH
Test if installation is success. Open a command window and change the directory to type-inference. Run javac -version if you have added it to your PATH or ./binary/javac if you didn't, it should output: javac 1.7.0-jsr308-1.3.0
Example use: inferring Reference Immutability and Method Purity
This is the implementation in the OOPSLA'12 paper.

Suppose that we want to do the inference for one test case included in type-inference-0.1.2.zip: inference-tests/CellClient.java.

Change the directory to type-inference
Run ./binary/javai-reim inference-tests/CellClient.java
The inference results are dumped to:

new-result.jaif: The result in JAIF format containing fields, parameters and return values, but no local variables.
new-result.csv: The result for all variables.
new-pure-methods.csv: The pure methods inferred by the tool.
Run benchmarks used in the OOPSLA'12 paper
Download 2012-oopsla-eval.zip
Unzip it into two directories: 2012-oopsla-eval and benchmarks
2012-oopsla-eval contains scripts for running the benchmarks
benchmarks contains all benchmarks used in the paper except SPECjbb.
Please make sure that JDK 6 is included in your PATH. JDK 7 would cause errors.
Example: execute the script run-jolden-ri in 2012-oopsla-eval to run the benchmark jolden
Use Eclipse Plugin to infer method purity for Java projects
Download the plugin and unzip it into plugins folder.
Copy the folder plugins/edu.rpi.cs.reiminfer_1.0.0 into your Eclipse plugins folder.
Add the following line in your eclipse.ini file: -Xbootclasspath/p:../../../plugins/edu.rpi.cs.reiminfer_1.0.0/lib/jsr308-all.jar
In Eclipse, right click a Java project and select ReimInfer? => Infer Pure Methods.
Build from source code
to be done

Instantiation of other type systems
To be done
