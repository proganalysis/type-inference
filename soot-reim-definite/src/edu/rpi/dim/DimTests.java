package edu.rpi.dim;

import org.junit.Test;

import junit.framework.TestCase;

public class DimTests extends TestCase {

	private static String RT_HOME = "/Users/ana/Desktop/Research/AlternateJDKs/jdk1.7.0_75.jdk/Contents/Home/jre/lib/rt.jar";
	
	private static boolean appOnly = false;
	
	// TODO: Toggle to set 2 different sets of experiments. Whole-program vs app only using annotated jdk.
	
	/*
	@Test
	public void test1() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/ CircularInitialization
		 
		if (appOnly) { 
			String[] args = new String[6];
			args[0] = "-app";
			args[1] = "-f";
			args[2] = "J";
			args[3] = "-cp";
			args[4] = "./jdk/android/jdk-android.jar:./tests/";
			args[5] = "CircularInitialization";
 			SootDefiniteImmutability.main(args);
 		}
 		else {
 			String[] args = new String[9];
			args[0] = "-app";
			args[1] = "-include"; // Needed for Whole-program
			args[2] = "java"; // Needed for Whole-program
			args[3] = "-allow-phantom-refs"; // Whole-program
			args[4] = "-f";
			args[5] = "J";
			args[6] = "-cp";
			args[7] = ./tests/:"+RT_HOME; // "./jdk/android/jdk-android.jar:./tests/";
			args[8] = "CircularInitialization";
 			SootDefiniteImmutability.main(args);
 		}
	}
	*/
	
	/*
	@Test
	public void test1() {
	
		if (appOnly) {	
			// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
			String[] args = new String[6];
			args[0] = "-app";
			args[1] = "-f";
			args[2] = "J";
			args[3] = "-cp";
			args[4] = "./jdk/android/jdk-android.jar:./tests/";
			args[5] = "Test4";
 			SootDefiniteImmutability.main(args);
 		}
 		else {
 			String[] args = new String[9];
			args[0] = "-app";
			args[1] = "-include"; // Needed for Whole-program
			args[2] = "java"; // Needed for Whole-program
			args[3] = "-allow-phantom-refs"; // Whole-program
			args[4] = "-f";
			args[5] = "J";
			args[6] = "-cp";
			args[7] = "./tests/:"+RT_HOME;  //"./jdk/android/jdk-android.jar:./tests/";
			args[8] = "Test4";
 			SootDefiniteImmutability.main(args);
 		}
	}
	*/
	
	
	/*
	@Test
	public void test1() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include"; // Needed for Whole-program
		args[2] = "java"; // Needed for Whole-program
		args[3] = "-allow-phantom-refs"; // Whole-program
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = "./tests/"+":"+RT_HOME; // "./jdk/android/jdk-android.jar:./tests/"; // RT_HOME is whole program
		args[8] = "LibraryUse";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
	// Small correctness reg tests
	
	/*
	@Test
	public void test1() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests1";
 		SootDefiniteImmutability.main(args);
 
	}
	*/
	
	/*	
	@Test
	public void test2() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests2";
 		SootDefiniteImmutability.main(args);
 		// Avg pt set call: 1.33
 		// Avg pt set field: 1.67 		
 		// Avg pt set: 1.33
	}
	*/
		
	/*
	@Test
	public void test3() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests3";
 		SootDefiniteImmutability.main(args);
 		
	}
	*/
		
	/*
	@Test
	public void test4() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests4";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test5() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests5";
 		SootObjectImmutability.main(args);
 		// Size: 97 calls, 42 fields 	
 		// Avg call: 1.30
 		// Avg field: 1.42
 		// Avg: 1.30
	}
	*/
	
	/*
	@Test
	public void test6() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests6";
 		SootObjectImmutability.main(args);
 		// Avg call: 1.48
 		// Avg field: 1.65
 		// Avg: 1.48
	}
	*/
	
	
	/*
	@Test
	public void test7() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests7";
 		SootObjectImmutability.main(args);
 		// Size: ?? calls, 14 fields.
 		// Avg call: 1
 		// Avg field: 1
 		// Avg: 1
	}
	*/
	
	/* //Soot fails, don't know why! 
	@Test
	public void test8() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests8";
 		SootObjectImmutability.main(args);
 		// Size: ?? calls, 14 fields.
 		// Avg calls: 1.35
 		// Avg fields: 1.18
 		// Avg: 1.18
	}	
	*/
	
	/*
	@Test
	public void test9() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/fop main2
		String[] args = new String[6];
		//args[0] = "-include";
		//args[1] = "java.util.";
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/eclipse-deps.jar:./tests/";
		args[5] = "CFLTest9";
 		SootObjectImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test10() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/fop main2
		String[] args = new String[6];
		//args[0] = "-include";
		//args[1] = "java.util.";
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests10";
 		SootObjectImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test10() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/fop main2
		String[] args = new String[6];
		//args[0] = "-include";
		//args[1] = "java.util.";
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/";
		args[5] = "CFLTests11";
 		SootObjectImmutability.main(args);
	}
	*/
	
	
	// Tests, javad and eclipsec
	
	/*
	@Test
	public void test9() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		if (!appOnly) {
			String[] args = new String[9];
			args[0] = "-app";
			args[1] = "-include"; // Needed for Whole-program
			args[2] = "java"; // Needed for Whole-program
			args[3] = "-allow-phantom-refs"; // Whole-program
			args[4] = "-f";
			args[5] = "J";
			args[6] = "-cp";
			args[7] = "./tests/benchmarks/javad"+":"+RT_HOME; // "./jdk/android/jdk-android.jar:./tests/benchmarks/javad";
			args[8] = "main";
 			SootDefiniteImmutability.main(args);
 		// 1596, 49 poly, 437 mutable, 14 maybemutable
 		}
 		else {
 			String[] args = new String[6];
			args[0] = "-app";
			args[1] = "-f";
			args[2] = "J";
			args[3] = "-cp";
			args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/javad";
			args[5] = "main";
 			SootDefiniteImmutability.main(args);
 		}
	}
	*/
		
	
	/*
	@Test
	public void test16() {
		if (!appOnly) {
			String[] args = new String[9];
			args[0] = "-app";
			args[1] = "-include"; // Needed for Whole-program
			args[2] = "java"; // Needed for Whole-program
			args[3] = "-allow-phantom-refs"; // Whole-program
			args[4] = "-f";
			args[5] = "J";
			args[6] = "-cp";
			args[7] = "./tests/benchmarks/eclipsec"+":"+RT_HOME; //"./jdk/android/jdk-android.jar:./tests/benchmarks/eclipsec";
			args[8] = "org.eclipse.jdt.internal.compiler.batch.Main";
			SootDefiniteImmutability.main(args);
		}
		else {
			String[] args = new String[6];
			args[0] = "-app";
			args[1] = "-f";
			args[2] = "J";
			args[3] = "-cp";
			args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/eclipsec";
			args[5] = "org.eclipse.jdt.internal.compiler.batch.Main";
			SootDefiniteImmutability.main(args);
		}
	}
	*/
	
	
	// DACAPO
	
	/*
	@Test
	public void test10() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		if (!appOnly) {
			String[] args = new String[11];
			args[0] = "-app";
			args[1] = "-dynamic-package";
			args[2] = "antlr.";
			args[3] = "-include"; // Needed for Whole-program
			args[4] = "java"; // Needed for Whole-program
			args[5] = "-allow-phantom-refs"; // Whole-program
			args[6] = "-f";
			args[7] = "J";
			args[8] = "-cp";
			args[9] = "./tests/benchmarks/antlr.jar:"+RT_HOME;
			args[10] = "dacapo.antlr.Main2";
			SootDefiniteImmutability.main(args);
		}
		else {
			String[] args = new String[8];
			args[0] = "-app";
			args[1] = "-dynamic-package";
			args[2] = "antlr.";
			args[3] = "-f";
			args[4] = "J";
			args[5] = "-cp";
			args[6] = "./jdk/android/jdk-android.jar:./tests/benchmarks/antlr.jar";
			args[7] = "dacapo.antlr.Main2";
			SootDefiniteImmutability.main(args);
		}
	}
	*/
	
	
	@Test	
	public void test11() { // COMPLETE. No reflection.
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/bloat main2
		if (!appOnly) {
			String[] args = new String[9];
			args[0] = "-app";
			args[1] = "-include"; // Needed for Whole-program
			args[2] = "java"; // Needed for Whole-program
			args[3] = "-allow-phantom-refs"; // Whole-program
			args[4] = "-f";
			args[5] = "J";
			args[6] = "-cp";
			args[7] = "./tests/benchmarks/bloat-deps.jar:./tests/benchmarks/bloat.jar:"+RT_HOME;
			args[8] = "dacapo.bloat.Main2";
 			SootDefiniteImmutability.main(args);
		}
		else {
			String[] args = new String[6];
			args[0] = "-app";
			args[1] = "-f";
			args[2] = "J";
			args[3] = "-cp";
			args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/bloat-deps.jar:./tests/benchmarks/bloat.jar";
			args[5] = "dacapo.bloat.Main2";
 			SootDefiniteImmutability.main(args);
 		}
	}
	
	
	/*
	@Test
	public void test12() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[11];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.jfree.chart.";
		//args[2] = "org.jfree.chart.axis.";
		//args[3] = "-dynamic-package";
		//args[4] = "org.jfree.chart.encoders.";
		//args[5] = "-dynamic-package";
		//args[6] = "org.jfree.chart.needle.";
		//args[7] = "-dynamic-package";
		//args[8] = "org.jfree.chart.plot.";
		//args[9] = "-dynamic-package";
		//args[10] = "org.jfree.chart.servlet.";
		args[3] = "-include"; // Needed for Whole-program
		args[4] = "java"; // Needed for Whole-program
		args[5] = "-allow-phantom-refs"; // Whole-program

		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		// args[14] = "./jdk/android/jdk-android.jar:./tests/benchmarks/chart-deps.jar:./tests/benchmarks/chart.jar";
		args[9] = "./tests/benchmarks/chart-deps.jar:./tests/benchmarks/chart.jar:"+RT_HOME;
		args[10] = "dacapo.chart.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test13() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[28];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.eclipse.";
		// fdfd
		args[3] = "org.eclipse.osgi.framework.adaptor.core.";
		args[4] = "-dynamic-package";
		args[5] = "org.osgi.framework.";
		args[6] = "-dynamic-package";
		args[7] = "org.eclipse.osgi.internal.profile.";
		args[8] = "-dynamic-package";
		args[9] = "org.eclipse.osgi.framework.internal.core.";
		args[10] = "-dynamic-package";
		args[11] = "org.eclipse.osgi.framework.internal.defaultadaptor.";
		args[12] = "-dynamic-package";
		args[13] = "org.osgi.service.condpermadmin.";
		args[14] = "-dynamic-package";
		args[15] = "org.osgi.service.url.";
		args[16] = "-dynamic-package";
		args[17] = "dacapo.";
		args[18] = "-dynamic-package";
		args[19] = "org.eclipse.osgi.framework.internal.protocol.reference.";
		
		//args[19] = "-dynamic-package";
		//args[20] = "org.eclipse.osgi.framework.internal.core.";
		
		args[20] = "-include"; // Needed for Whole-program
		args[21] = "java"; // Needed for Whole-program
		args[22] = "-allow-phantom-refs"; // Whole-program
		
		args[23] = "-f";
		args[24] = "J";
		args[25] = "-cp";
		// args[24] = "./jdk/android/jdk-android.jar:./tests/benchmarks/eclipse-deps.jar:./tests/benchmarks/eclipse.jar";
		args[26] = "./tests/benchmarks/eclipse-deps.jar:./tests/benchmarks/eclipse.jar:"+RT_HOME;
		args[27] = "dacapo.eclipse.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test14() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/fop main2
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include"; // Needed for Whole-program
		args[2] = "java"; // Needed for Whole-program
		args[3] = "-allow-phantom-refs"; // Whole-program
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		// args[7] = "./jdk/android/jdk-android.jar:./tests/benchmarks/fop-deps.jar:./tests/benchmarks/fop.jar";
		args[7] = "./tests/benchmarks/fop-deps.jar:./tests/benchmarks/fop.jar:"+RT_HOME;
		args[8] = "dacapo.fop.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test15() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/fop main2
		String[] args = new String[10];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.hsqldb.";
		args[3] = "-dynamic-package";
		args[4] = "org.hsqldb.resources.";
		args[5] = "-f";
		args[6] = "J";
		args[7] = "-cp";
		args[8] = "./jdk/android/jdk-android.jar:./tests/benchmarks/hsqldb-deps.jar:./tests/benchmarks/hsqldb.jar";
		args[9] = "dacapo.hsqldb.Main";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test16() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/luindex main2
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java.";
		args[3] = "-allow-phantom-refs";		
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		//args[7] = "./tests/benchmarks/rt-1.4.2_11.jar:./tests/benchmarks/luindex-deps.jar:./tests/benchmarks/luindex.jar";
		
		args[7] = "./tests/benchmarks/luindex-deps.jar:./tests/benchmarks/luindex.jar:"+RT_HOME;
		args[8] = "dacapo.luindex.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
	/*
	@Test
	public void test17() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/lusearch main2
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java.";
		args[3] = "-allow-phantom-refs";	
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		//args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/lusearch-deps.jar:./tests/benchmarks/lusearch.jar";
		args[7] = "./tests/benchmarks/lusearch-deps.jar:./tests/benchmarks/lusearch.jar:"+RT_HOME;
		args[8] = "dacapo.lusearch.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	

	
	/*
	@Test 
	public void test18() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/pmd main2
		String[] args = new String[11];
		args[0] = "-app";
		args[1] = "-include"; // Needed for Whole-program
		args[2] = "java"; // Needed for Whole-program
		args[3] = "-allow-phantom-refs"; // Whole-program
		
		args[4] = "-dynamic-package";
		args[5] = "net.sourceforge.pmd.";
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		// args[6] = "./jdk/android/jdk-android.jar:./tests/benchmarks/pmd-deps.jar:./tests/benchmarks/pmd.jar";
		
		args[9] = "./tests/benchmarks/pmd-deps.jar:./tests/benchmarks/pmd.jar:"+RT_HOME;
		args[10] = "dacapo.pmd.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
	/*
	@Test  
	public void test19() { // COPMLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/xalan main2
		String[] args = new String[11];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.apache.xalan.";
		args[3] = "-include"; // Needed for Whole-program
		args[4] = "java"; // Needed for Whole-program
		args[5] = "-allow-phantom-refs"; // Whole-program
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./tests/benchmarks/xalan-deps.jar:./tests/benchmarks/xalan.jar:"+RT_HOME;
		args[10] = "dacapo.xalan.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
	/*
	// JDK test. Infers types for vars in java. package. 
	// Will need to run in no-locals mode. I.e., change inferencetraformer to ignore locals.
	@Test  
	public void test20() { // COPMLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/xalan main2
		String[] args = new String[10];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java.util.";
		args[3] = "-allow-phantom-refs";
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = RT_HOME;
		args[8] = "-process-dir";
		args[9] = RT_HOME;
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	// JDK test. Infers types for vars in java. package. 
	// Will need to run in no-locals mode. I.e., change inferencetraformer to ignore locals.
	@Test  
	public void test20() { // COPMLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/xalan main2
		String[] args = new String[10];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java.lang.";
		args[3] = "-allow-phantom-refs";
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = RT_HOME;
		args[8] = "-process-dir";
		args[9] = RT_HOME;
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	// LIBRARIES FROM Javarifier/ReIm
	
	/*
	@Test  
	public void test21() { // COPMLETE
		String[] args = new String[14];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java";
		args[3] = "-allow-phantom-refs";
		args[4] = "-src-prec";
		args[5] = "class";
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./tests/benchmarks/tinySQL/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./tests/benchmarks/tinySQL/";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test  
	public void test21() { // COPMLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/xalan main2
		String[] args = new String[14];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java";
		args[3] = "-allow-phantom-refs";
		args[4] = "-src-prec";
		args[5] = "class";
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./tests/benchmarks/commons-pool-1.2/build/classes/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./tests/benchmarks/commons-pool-1.2/build/classes/";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test  
	public void test21() { // COPMLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/xalan main2
		String[] args = new String[14];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java";
		args[3] = "-allow-phantom-refs";
		args[4] = "-src-prec";
		args[5] = "class";
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./tests/benchmarks/jtds-1.0/build/classes/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./tests/benchmarks/jtds-1.0/build/classes/";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
	/*
	@Test  
	public void test22() { // COPMLETE
		String[] args = new String[14];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java";
		args[3] = "-allow-phantom-refs";
		args[4] = "-src-prec";
		args[5] = "class";
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./tests/benchmarks/htmlparser/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./tests/benchmarks/htmlparser/";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/

	/*
	@Test  
	public void test22() { // COPMLETE
		String[] args = new String[14];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java";
		args[3] = "-allow-phantom-refs";
		args[4] = "-src-prec";
		args[5] = "class";
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./tests/benchmarks/jdbf-0.1.1/src:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./tests/benchmarks/jdbf-0.1.1/src";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test  
	public void test22() { // COPMLETE
		String[] args = new String[14];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java";
		args[3] = "-allow-phantom-refs";
		args[4] = "-src-prec";
		args[5] = "class";
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./tests/benchmarks/jdbm-1.0/src/main/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./tests/benchmarks/jdbm-1.0/src/main";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/

	// JOLDEN
	
	/*
	@Test  
	public void test23() { // COPMLETE
		// JOlden
		String[] args = new String[11];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java";
		args[3] = "-allow-phantom-refs";
		args[4] = "-src-prec";
		args[5] = "class";
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./tests/benchmarks/jolden/:"+RT_HOME;
		// args[10] = "bh.BH"; // 229 READONLY, 71 POLYREAD, 229 MUTABLE; 59/0; 0.80
						   // 60 READONLY, 61 POLYREAD, 41 MUTABLE, 13/0, 0.759
		// args[10] = "bisort.BiSort"; // 34 READONLY, 3 POLYREAD, 58 MUTABLE; 6/0; 0.90
								   // 8 READONLY, 3 POLYREAD, 7 MUTABLE; 0/0; 1.00
		// args[10] = "em3d.Em3d"; // 60 READONLY, 30 POLYREAD, 104 MUTABLE; 30/0; 0.78
						       // 17 READONLY, 26 POLYREAD, 9 MUTABLE; 6/0; 0.6
		// args[10] = "health.Health"; // 78 READONLY, 27 POLYREAD, 136 MUTABLE; 33/0; 0.80
								// 25 READONLY, 23 POLYREAD, 19 MUTABLE; 6/0; 0.76
		// args[10] = "mst.MST"; // 81 READONLY, 45 POLYREAD, 83 MUTABLE; 25/0; 0.77
							 // 29 READONLY, 33 POLYREAD, 16 MUTABLE; 5/0; 0.761
		// args[10] = "perimeter.Perimeter"; // 245 READONLY, 0 POLYREAD, 44 MUTABLE; 0/0; 1.0
										// 101 READONLY, 0 POLYREAD, 6 MUTABLE; 0/0; 1.0
		// args[10] = "power.Power"; // 112 READONLY, 20 POLYREAD, 126 MUTABLE; 2/0; 0.98
								// 28 READONLY, 20 POLYREAD, 21 MUTABLE; 0/0; 1.0
		// args[10] = "treeadd.TreeAdd"; //46 READONLY, 0 POLYREAD, 35 MUTABLE; 0/0; 1.0
									// 17 READONLY, 0 POLYREAD, 5 MUTABLE; 0/0; 1.0
	    // args[10] = "tsp.TSP"; // 37 READONLY, 13 POLYREAD, 83 MUTABLE; 6/1; 0.92
	    								// 10 READONLY, 9 POLYREAD, 8 MUTABLE; 2/0; 0.8
		args[10] = "voronoi.Voronoi"; // 286 READONLY, 72 POLYREAD, 171 MUTABLE; 45/0; 0.79
									// 80 READONLY, 40 POLYREAD, 27 MUTABLE; 10/0; 0.729
		                                // TOTAL: 1208 READONLY, 281 POLYREAD, 1069 MUTABLE; 206/0; 0.84
									   // TOTAL: 375 READONLY, 215 POLYREAD, 159 MUTABLE; 42/0; 0.79 
		// args[12] = "-d";
		// args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
}
