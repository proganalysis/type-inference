package edu.rpi.dim;

import org.junit.Test;

import junit.framework.TestCase;

public class OimTests extends TestCase {

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
		args[5] = "CircularInitialization";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
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
		args[5] = "Test4";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
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
		args[5] = "LibraryUse";
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
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/javad";
		args[5] = "main";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
	/*
	@Test
	public void test16() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/eclipsec";
		args[5] = "org.eclipse.jdt.internal.compiler.batch.Main";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	// DACAPO
	
	/*
	@Test
	public void test10() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
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
	*/
	
	
	@Test	
	public void test11() { // COMPLETE. No reflection.
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/bloat main2
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/bloat-deps.jar:./tests/benchmarks/bloat.jar";
		args[5] = "dacapo.bloat.Main2";
 		SootDefiniteImmutability.main(args);
	}
	
	
	/*
	@Test
	public void test12() {
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[10];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.jfree.chart.axis.";
		args[3] = "-dynamic-package";
//		args[10] = "org.jfree.chart.encoders.";
//		args[11] = "-dynamic-package";
//		args[12] = "org.jfree.chart.needle.";
//		args[13] = "-dynamic-package";
		args[4] = "org.jfree.chart.plot.";
//		args[15] = "-dynamic-package";
//		args[16] = "org.jfree.chart.servlet.";


		args[5] = "-f";
		args[6] = "J";
		args[7] = "-cp";
		args[8] = "./jdk/android/jdk-android.jar:./tests/benchmarks/chart-deps.jar:./tests/benchmarks/chart.jar";
		args[9] = "dacapo.chart.Main2";
 		SootObjectImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test13() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/javad main
		String[] args = new String[24];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.eclipse.osgi.framework.adaptor.core.";
		args[3] = "-dynamic-package";
		args[4] = "org.osgi.framework.";
		args[5] = "-dynamic-package";
		args[6] = "org.eclipse.osgi.internal.profile.";
		args[7] = "-dynamic-package";
		args[8] = "org.eclipse.osgi.framework.internal.core.";
		args[9] = "-dynamic-package";
		args[10] = "org.eclipse.osgi.framework.internal.defaultadaptor.";
		args[11] = "-dynamic-package";
		args[12] = "org.osgi.service.condpermadmin.";
		args[13] = "-dynamic-package";
		args[14] = "org.osgi.service.url.";
		args[15] = "-dynamic-package";
		args[16] = "dacapo.";
		args[17] = "-dynamic-package";
		args[18] = "org.eclipse.osgi.framework.internal.protocol.reference.";
		args[19] = "-f";
		args[20] = "J";
		args[21] = "-cp";
		args[22] = "./jdk/android/jdk-android.jar:./tests/benchmarks/eclipse-deps.jar:./tests/benchmarks/eclipse.jar";
		args[23] = "dacapo.eclipse.Main2";
 		SootObjectImmutability.main(args);
	}
	*/
	
	
	/*
	@Test
	public void test14() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/fop main2
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/fop-deps.jar:./tests/benchmarks/fop.jar";
		args[5] = "dacapo.fop.Main2";
 		SootObjectImmutability.main(args);
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
 		SootObjectImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test16() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/luindex main2
		String[] args = new String[6];
		//args[0] = "-include";
		//args[1] = "java.";
		//args[2] = "-allow-phantom-refs";
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./tests/benchmarks/rt-1.4.2_11.jar:./tests/benchmarks/luindex-deps.jar:./tests/benchmarks/luindex.jar";
		args[5] = "dacapo.luindex.Main2";
 		SootObjectImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test17() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/lusearch main2
		String[] args = new String[6];
		args[0] = "-app";
		args[1] = "-f";
		args[2] = "J";
		args[3] = "-cp";
		args[4] = "./jdk/android/jdk-android.jar:./tests/benchmarks/lusearch-deps.jar:./tests/benchmarks/lusearch.jar";
		args[5] = "dacapo.lusearch.Main2";
 		SootObjectImmutability.main(args);
	}
	*/

	/*
	@Test // seems ok 
	public void test18() { // COMPLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/fop main2
		String[] args = new String[8];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "net.sourceforge.pmd.";
		args[3] = "-f";
		args[4] = "J";
		args[5] = "-cp";
		args[6] = "./jdk/android/jdk-android.jar:./tests/benchmarks/pmd-deps.jar:./tests/benchmarks/pmd.jar";
		args[7] = "dacapo.pmd.Main2";
 		SootObjectImmutability.main(args);
	}
	*/
	
	/*
	@Test  
	public void test19() { // COPMLETE
		// -app -f J -cp ./jdk/android/jdk-android.jar:./tests/benchmarks/xalan main2
		String[] args = new String[8];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.apache.xalan.";
		args[3] = "-f";
		args[4] = "J";
		args[5] = "-cp";
		args[6] = "./jdk/android/jdk-android.jar:./tests/benchmarks/xalan-deps.jar:./tests/benchmarks/xalan.jar";
		args[7] = "dacapo.xalan.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
		
	
}
