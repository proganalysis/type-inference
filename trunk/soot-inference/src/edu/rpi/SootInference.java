package edu.rpi;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.PrintStream;
import java.io.File;

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
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.*;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.tagkit.*; 
import edu.rpi.sflow.*;
import edu.rpi.reim.*;


public class SootInference {
	
	public static void main(String[] args) {
		
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
            "com.nbpcorp.mobilead",
            "com.crashlytics",
            "com.inmobi.androidsdk",
            "com.millennialmedia",
            "com.admob",
            "com.slidingmenu",
//            "com.facebook",
            "com.admarvel.android.ads",
            "com.amazon.inapp.purchasing",
            "com.loopj",
//            "com.dropbox",
            "net.daum.adam.publisher",
            "twitter4j.",
            "org.java_websocket",
            "org.acra",
            "org.apache"
        };
        List<String> exclude = new ArrayList<String>(Arrays.asList(excludes));
        Options.v().set_exclude(exclude);

        InferenceTransformer reimTransformer = new ReimTransformer();
        InferenceTransformer sflowTransformer = new SFlowTransformer();
        PackManager.v().getPack("jtp").add(new Transform("jtp.reim", reimTransformer));
        PackManager.v().getPack("jtp").add(new Transform("jtp.sflow", sflowTransformer));

		soot.Main.main(args);

        System.out.println(String.format("%6s: %14d", "size", AnnotatedValueMap.v().size()));
        System.out.println(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024*1024)));
        System.out.println(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024*1024)));

        String outputDir = SourceLocator.v().getOutputDir();

        System.out.println("INFO: Solving Reim constraints:  " + reimTransformer.getConstraints().size() + " in total...");
        ConstraintSolver cs = new SetbasedSolver(reimTransformer);
        Set<Constraint> errors = cs.solve();
        try {
            PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-constraints.log");
            for (Constraint c : reimTransformer.getConstraints()) {
                reimOut.println(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Constraint c : errors)
            System.out.println(c);
        System.out.println("INFO: Finish solving Reim constraints. " + errors.size() + " error(s)");

        try {
            PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-result.jaif");
            reimTransformer.printJaif(reimOut);
            reimTransformer.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Solving SFlow constraints:  " + sflowTransformer.getConstraints().size() + " in total...");
        ConstraintSolver sflowSolver = new SFlowConstraintSolver2(sflowTransformer);
        errors = sflowSolver.solve();
        try {
            PrintStream sflowOut = new PrintStream(outputDir + File.separator + "sflow-constraints.log");
            for (Constraint c : sflowTransformer.getConstraints()) {
                sflowOut.println(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        for (Constraint c : errors)
            System.out.println(c + "\n");
        System.out.println("INFO: Finish solving SFlow constraints. " + errors.size() + " error(s)");
        try {
            PrintStream sflowOut = new PrintStream(outputDir + File.separator + "sflow-result.jaif");
            sflowTransformer.printJaif(sflowOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Annotated value size: " + AnnotatedValueMap.v().size());
		
	}
}
