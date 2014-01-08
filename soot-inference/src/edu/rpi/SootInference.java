package edu.rpi;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_keep_line_number(true);

        Scene.v().addBasicClass("java.lang.StringBuilder", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.io.InputStreamReader",SootClass.HIERARCHY);
//        Scene.v().addBasicClass("java.lang.IllegalStateException", SootClass.HIERARCHY);
//        Scene.v().addBasicClass("java.lang.UnsupportedOperationException", SootClass.HIERARCHY);
		

        InferenceTransformer sflowTransformer = new SFlowTransformer();
        InferenceTransformer reimTransformer = new ReimTransformer();
        PackManager.v().getPack("jtp").add(new Transform("jtp.reim", reimTransformer));
        PackManager.v().getPack("jtp").add(new Transform("jtp.sflow", sflowTransformer));

		soot.Main.main(args);

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
        System.out.println("INFO: Finish solving Reim constraints. There are " + errors.size() + " errors");

        try {
            PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-result.jaif");
            reimTransformer.printJaif(reimOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Solving SFlow constraints:  " + sflowTransformer.getConstraints().size() + " in total...");
        ConstraintSolver sflowSolver = new SFlowConstraintSolver(sflowTransformer, reimTransformer.getAnnotatedValues());
        errors = sflowSolver.solve();
        try {
            PrintStream sflowOut = new PrintStream(outputDir + File.separator + "sflow-constraints.log");
            for (Constraint c : sflowTransformer.getConstraints()) {
                sflowOut.println(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("INFO: Finish solving SFlow constraints. There are " + errors.size() + " errors");
        for (Constraint c : errors)
            System.out.println(c);

        try {
            PrintStream sflowOut = new PrintStream(outputDir + File.separator + "sflow-result.jaif");
            sflowTransformer.printJaif(sflowOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
}
