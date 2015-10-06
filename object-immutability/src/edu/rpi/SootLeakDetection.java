package edu.rpi;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.PrintStream;
import java.io.File;
import java.lang.annotation.Annotation;

import checkers.inference.reim.quals.Readonly;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SourceLocator;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.options.Options;
import soot.tagkit.*; 
import edu.rpi.sflow.*;
import edu.rpi.reim.*;
import static com.esotericsoftware.minlog.Log.*;


public class SootLeakDetection {
	
	public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
		        
		//prefer Android APK files// -src-prec apk
//        Options.v().set_src_prec(Options.src_prec_apk);
		
		//output as APK, too//-f J
//        Options.v().set_output_format(Options.output_format_none);
//        Options.v().set_output_format(Options.output_format_jimple);
//        Options.v().set_keep_line_number(true);

        // Exclude packages
        String[] excludes = new String[] {
            "android.annotation",
            "android.hardware",
            "android.support",
            "android.media", 
            "com.android",
            "android.bluetooth", 
            "android.media",
            "com.google",
            "com.yume.android",
            "com.squareup.okhttp",
            "com.crashlytics",
//            "com.nbpcorp.mobilead", // ad
//            "com.inmobi.androidsdk", //ad
//            "com.millennialmedia", //ad
//            "com.admob",  //ad
//            "com.admarvel.android.ads",  // ad
//            "com.mopub.mobileads",  //ad
//            "com.medialets", // ad
            "com.slidingmenu",
            "com.amazon.inapp.purchasing",
            "com.loopj",
            "com.appbrain",
            "com.heyzap.sdk",
            "net.daum.adam.publisher",
            "twitter4j.",
            "org.java_websocket",
            "org.acra",
            "org.apache"
        };
        List<String> exclude = new ArrayList<String>(Arrays.asList(excludes));
        Options.v().set_exclude(exclude);

        set(LEVEL_DEBUG);


        InferenceTransformer leakTransformer = new LeakTransformer();

        PackManager.v().getPack("jtp").add(new Transform("jtp.reim", leakTransformer));    
        Scene.v().addBasicClass("java.io.FileFilter",SootClass.SIGNATURES);
        Scene.v().addBasicClass("javax.swing.event.ChangeListener",SootClass.SIGNATURES);
        Scene.v().addBasicClass("javax.swing.event.ListSelectionListener",SootClass.SIGNATURES);
		soot.Main.main(args);

        info(String.format("%6s: %14d", "size", AnnotatedValueMap.v().size()));
        info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024*1024)));
        info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024*1024)));

        String outputDir = SourceLocator.v().getOutputDir();

        boolean needTrace = !(System.getProperty("noTrace") != null);

        System.out.println("INFO: Solving Reim constraints:  " + leakTransformer.getConstraints().size() + " in total...");
        ConstraintSolver cs = new SetbasedSolver(leakTransformer, false);
        Set<Constraint> errors = cs.solve();
        try {
            PrintStream leakOut = new PrintStream(outputDir + File.separator + "leak-constraints.log");
            for (Constraint c : leakTransformer.getConstraints()) {
                leakOut.println(c);                
                // Count # of allocs vs # of readonly allocs
                leakOut.println(c.getLeft().getKind()+"\t"+c.getRight().getKind());
            }
            leakOut.close(); // ANA            
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Constraint c : errors)
            System.out.println(c);
        System.out.println("INFO: Finish solving Leak constraints. " + errors.size() + " error(s)");

        try {
           PrintStream reimOut = new PrintStream(outputDir + File.separator + "leak-result.jaif");
	
           leakTransformer.printJaif(reimOut); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        ReimUtils.collectLeakedThisData((LeakTransformer) leakTransformer);
        
        leakTransformer.clear();        

//      System.out.println("INFO: Annotated value size: " + AnnotatedValueMap.v().size());
		
        long endTime   = System.currentTimeMillis();
        System.out.println("INFO: Total running time: " + ((float)(endTime - startTime) / 1000) + " sec");
	}
}
