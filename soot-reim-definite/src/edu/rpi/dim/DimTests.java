package edu.rpi.dim;

import org.junit.Test;

import junit.framework.TestCase;

public class DimTests extends TestCase {

	private static String RT_HOME = "/Users/ana/Desktop/Research/AlternateJDKs/jdk1.7.0_75.jdk/Contents/Home/jre/lib/rt.jar";
			
/* == javad and ejc. Benchmarks from ReImInfer == */
	
	/*
	@Test
	public void test1() {
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include"; // Whole-program analysis
		args[2] = "java"; // Whole-program analysis
		args[3] = "-allow-phantom-refs"; // Whole-program
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = "./bench/javad"+":"+RT_HOME;
		args[8] = "main";
 		SootDefiniteImmutability.main(args);
  		// 1596, 49 poly, 437 mutable, 14 maybemutable
	}
	*/
			
	/*
	@Test
	public void test2() {
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include"; // Needed for Whole-program
		args[2] = "java"; // Needed for Whole-program
		args[3] = "-allow-phantom-refs"; // Whole-program
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = "./bench/eclipsec"+":"+RT_HOME;
		args[8] = "org.eclipse.jdt.internal.compiler.batch.Main";
		SootDefiniteImmutability.main(args);
	}
	*/
	
	
/* == DACAPO 2006 == */
	
	/*
	@Test
	public void test3() {
		String[] args = new String[11];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "antlr."; // Loading entire package, reflection
		args[3] = "-include"; // Needed for Whole-program
		args[4] = "java"; // Needed for Whole-program
		args[5] = "-allow-phantom-refs"; // Whole-program
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./bench/antlr.jar:"+RT_HOME;
		args[10] = "dacapo.antlr.Main2";
		SootDefiniteImmutability.main(args);
	}
	*/
		
	@Test	
	public void test4() {
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include"; // Needed for Whole-program
		args[2] = "java"; // Needed for Whole-program
		args[3] = "-allow-phantom-refs"; // Whole-program
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = "./bench/bloat-deps.jar:./bench/bloat.jar:"+RT_HOME;
		args[8] = "dacapo.bloat.Main2";
 		SootDefiniteImmutability.main(args);
	}
	
	
	/*
	@Test
	public void test5() {
		String[] args = new String[11];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.jfree.chart.";
		args[3] = "-include"; // Needed for Whole-program
		args[4] = "java"; // Needed for Whole-program
		args[5] = "-allow-phantom-refs"; // Whole-program
		args[6] = "-f";
		args[7] = "J";
		args[8] = "-cp";
		args[9] = "./bench/chart-deps.jar:./bench/chart.jar:"+RT_HOME;
		args[10] = "dacapo.chart.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test6() {
		String[] args = new String[28];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.eclipse.";
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
				
		args[20] = "-include"; // Needed for Whole-program
		args[21] = "java"; // Needed for Whole-program
		args[22] = "-allow-phantom-refs"; // Whole-program
		
		args[23] = "-f";
		args[24] = "J";
		args[25] = "-cp";
		args[26] = "./bench/eclipse-deps.jar:./bench/eclipse.jar:"+RT_HOME;
		args[27] = "dacapo.eclipse.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test7() {
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include"; // Needed for Whole-program
		args[2] = "java"; // Needed for Whole-program
		args[3] = "-allow-phantom-refs"; // Whole-program
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = "./bench/fop-deps.jar:./bench/fop.jar:"+RT_HOME;
		args[8] = "dacapo.fop.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test8() {
		String[] args = new String[10];
		args[0] = "-app";
		args[1] = "-dynamic-package";
		args[2] = "org.hsqldb.";
		args[3] = "-dynamic-package";
		args[4] = "org.hsqldb.resources.";
		args[5] = "-f";
		args[6] = "J";
		args[7] = "-cp";
		args[8] = "./bench/hsqldb-deps.jar:./bench/hsqldb.jar:"+RT_HOME;
		args[9] = "dacapo.hsqldb.Main";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test
	public void test9() { // COMPLETE
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java.";
		args[3] = "-allow-phantom-refs";		
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = "./bench/luindex-deps.jar:./bench/luindex.jar:"+RT_HOME;
		args[8] = "dacapo.luindex.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
		
	/*
	@Test
	public void test10() {
		String[] args = new String[9];
		args[0] = "-app";
		args[1] = "-include";
		args[2] = "java.";
		args[3] = "-allow-phantom-refs";	
		args[4] = "-f";
		args[5] = "J";
		args[6] = "-cp";
		args[7] = "./bench/lusearch-deps.jar:./bench/lusearch.jar:"+RT_HOME;
		args[8] = "dacapo.lusearch.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	

	
	/*
	@Test 
	public void test11() { // COMPLETE
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
		args[9] = "./bench/pmd-deps.jar:./bench/pmd.jar:"+RT_HOME;
		args[10] = "dacapo.pmd.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
	/*
	@Test  
	public void test12() { // COPMLETE
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
		args[9] = "./bench/xalan-deps.jar:./bench/xalan.jar:"+RT_HOME;
		args[10] = "dacapo.xalan.Main2";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
/* === JDK tests. java.util. and java.lang === */

	/*
	// JDK test. Infers types for vars in java.util. package. 
	@Test  
	public void test13() {
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
	// JDK test. Infers types for vars in java.lang package. 
	@Test  
	public void test14() {
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
	

/* === Benchmarks from Javarifier and ReIm === 
 * === Tests 15 through  */
	
	/*
	@Test  
	public void test15() { // COPMLETE
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
		args[9] = "./bench/tinySQL/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./bench/tinySQL/";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test  
	public void test16() {
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
		args[9] = "./bench/commons-pool-1.2/build/classes/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./bench/commons-pool-1.2/build/classes/";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test  
	public void test17() { 
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
		args[9] = "./bench/jtds-1.0/build/classes/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./bench/jtds-1.0/build/classes/";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	
	/*
	@Test  
	public void test18() { // COPMLETE
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
		args[9] = "./bench/htmlparser/:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./bench/htmlparser/";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/

	/*
	@Test  
	public void test19() { 
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
		args[9] = "./bench/jdbf-0.1.1/src:"+RT_HOME;
		args[10] = "-process-dir";
		args[11] = "./bench/jdbf-0.1.1/src";
		args[12] = "-d";
		args[13] = "/tmp/sootOutput";
 		SootDefiniteImmutability.main(args);
	}
	*/
	
	/*
	@Test  
	public void test20() { // COPMLETE
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
		args[9] = "./bench/jdbm-1.0/src/main/:"+RT_HOME;
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
	public void test21() { // COPMLETE
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
		args[9] = "./bench/jolden/:"+RT_HOME;
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
