package edu.rpi.dim;

import java.util.HashMap;
import java.util.Set;

import checkers.inference.reim.quals.Readonly;
import checkers.inference.reim.quals.Polyread;
import checkers.inference.reim.quals.MaybeMutable;
import checkers.inference.reim.quals.PolyOrMaybe;

import java.io.PrintStream;
import java.io.File;

import soot.PackManager;
import soot.PrimType;
import soot.Scene;
import soot.SourceLocator;
import soot.SootClass;
import soot.Transform;
import edu.rpi.AnnotatedValue;
import edu.rpi.AnnotatedValueMap;
import edu.rpi.AnnotationUtils;
import edu.rpi.Constraint;
import edu.rpi.ConstraintSolver;
import edu.rpi.InferenceTransformer;
import edu.rpi.SetbasedSolver;
import edu.rpi.reim.*;
import edu.rpi.sflow.SFlowConstraintSolver;
import edu.rpi.sflow.SFlowTransformer;

import static com.esotericsoftware.minlog.Log.*;


public class SootDefiniteImmutability {

	/*
	private static void tackTransformer(String name, InferenceTransformer transformer, String[] args, boolean solveConstraints) {
		
		System.out.println("Starting a new "+name+" transfromer.");
		
		PackManager.v().getPack("jtp").add(new Transform("jtp."+name, transformer));    
        Scene.v().addBasicClass("java.io.FileFilter",SootClass.SIGNATURES);
        Scene.v().addBasicClass("javax.swing.event.ChangeListener",SootClass.SIGNATURES);
        Scene.v().addBasicClass("javax.swing.event.ListSelectionListener",SootClass.SIGNATURES);
		
        soot.Main.main(args);

        info(String.format("%6s: %14d", "size", AnnotatedValueMap.v().size()));
        info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024*1024)));
        info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024*1024)));

        String outputDir = SourceLocator.v().getOutputDir();

        // Printing constraints
        printConstraints(name, transformer, outputDir);
        
        // Plain transformer only needs to generate constraints, no need to solve.
        // Other transformers need to solve.
        if (!solveConstraints) return;
        
        System.out.println("INFO: Solving "+name+" constraints:  " + transformer.getConstraints().size() + " in total...");
        ConstraintSolver cs = new SetbasedSolver(transformer, false);
        Set<Constraint> errors = cs.solve();
        printConstraints(name, transformer, outputDir);
        for (Constraint c : errors) {
            System.out.println(c);
            System.out.print("---LHS type: "); printAnnotations(c.getLeft(),transformer);
            System.out.print("---RHS type: "); printAnnotations(c.getRight(),transformer); 
        }
        System.out.println("INFO: Finish solving "+name+" constraints. " + errors.size() + " error(s)");

        try {
           PrintStream transOut = new PrintStream(outputDir + File.separator + name+"-result.jaif");	
           transformer.printJaif(transOut); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
                        
	}

	protected static void printConstraints(String name, InferenceTransformer transformer, String outputDir) {
		try {
            PrintStream Out = new PrintStream(outputDir + File.separator + name+"-constraints.log");
            for (Constraint c : transformer.getConstraints()) {
                Out.println(c);                
                Out.println(c.getLeft().getKind()+"\t"+c.getRight().getKind());
            }
            Out.close(); // ANA            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private static void printAnnotations(AnnotatedValue a, InferenceTransformer transformer) {
		if (a instanceof AnnotatedValue.AdaptValue) {
			AnnotatedValue.AdaptValue av = (AnnotatedValue.AdaptValue) a;
			System.out.println(av.getContextValue().getAnnotations(transformer) +" |> "+av.getDeclValue().getAnnotations(transformer));
		}
		else {
			System.out.println(a.getAnnotations(transformer));
		}
	}
	*/
	
	private static boolean isLibraryVar(AnnotatedValue av) {
			
		if (av.getEnclosingClass().isJavaLibraryClass()) 
			return true;
		else 
			return false;
				
	}
	
	private static boolean ignorePrefix(String s) {
		if (s.startsWith("fake-") || s.startsWith("lib-"))
			return true;
		else 
			return false;
	}
	
	private static boolean isPrimitiveType(AnnotatedValue av) {
		if (av.getType() instanceof PrimType) 
			return true;
		else
			return false;
	}
	
	private static void printTypeInfo(HashMap<String,AnnotatedValue> vm, boolean verbose) {
		//	System.out.println("INFO: ");
		int readonly = 0;
		int polyread = 0;
		int maybe = 0;
		int mutable = 0;
		int polyOrMaybe = 0;
		for (String v : vm.keySet()) {
			AnnotatedValue av = vm.get(v);
			if (isLibraryVar(av) || ignorePrefix(v) || isPrimitiveType(av)) continue;
			if (av.containsAnno(AnnotationUtils.fromClass(Readonly.class))){
				readonly++;
				if (verbose) System.out.println(v + " is readonly");
			}
			else if (av.containsAnno(AnnotationUtils.fromClass(MaybeMutable.class))) {
				maybe++;
				if (verbose) System.out.println(v + " is maybemutable");
			}
			else if (av.containsAnno(AnnotationUtils.fromClass(Polyread.class))) {
				polyread++;
				if (verbose) System.out.println(v + " is polyread");
			}
			else if (av.containsAnno(AnnotationUtils.fromClass(PolyOrMaybe.class))) {
				polyOrMaybe++;
				if (verbose) System.out.println(v + " is polyOrMaybe");
			}
			else {
				mutable++;
				if (verbose) System.out.println(v + " is mutable");
			}
		}
		System.out.println("INFO: Annotated value size: " + vm.size());
		System.out.println("INFO: "+readonly+" READONLY, "+polyread+" POLYREAD, "+mutable+" MUTABLE.");
		System.out.println("INFO: readonly/all: "+ ((float) readonly)/(readonly+mutable+maybe+polyOrMaybe+polyread));
		System.out.println("INFO: mutable/all: "+ ((float) mutable)/(readonly+mutable+maybe+polyOrMaybe+polyread));
		System.out.println("INFO: "+maybe+" MAYBEMUTABLE.");
		System.out.println("INFO: "+polyOrMaybe+" POLYORMAYBE. ");
		
		System.out.println("INFO: DEF_MUT/ALL_MUT: "+((float) mutable)/(maybe+polyOrMaybe+mutable));
	}
	
	public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
		
        set(LEVEL_DEBUG);


        InferenceTransformer reimTransformer = new DimTransformer(); // to change to Reim
        // InferenceTransformer sflowTransformer = new SFlowTransformer();
        PackManager.v().getPack("jtp").add(new Transform("jtp.reim", reimTransformer));
        // PackManager.v().getPack("jtp").add(new Transform("jtp.sflow", sflowTransformer));

		soot.Main.main(args);

        info(String.format("%6s: %14d", "size", AnnotatedValueMap.v().size()));
        info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024*1024)));
        info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024*1024)));

        String outputDir = SourceLocator.v().getOutputDir();

        System.out.println("INFO: Solving Reim constraints:  " + reimTransformer.getConstraints().size() + " in total...");
        ConstraintSolver cs = new SetbasedSolver(reimTransformer, false);
        Set<Constraint> errors = cs.solve();
        try {
            PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-constraints.log");
            for (Constraint c : reimTransformer.getConstraints()) {
                reimOut.println(c); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
                                        
        printTypeInfo(AnnotatedValueMap.v(), true);

        for (Constraint c : errors) {
            System.out.println(c); // TODO: figure this out, why are there any errors... I think it's handling of inner classes.
        }
        
        System.out.println("INFO: Finish solving Reim constraints. " + errors.size() + " error(s)");

        // printTypeInfo(AnnotatedValueMap.v(), true);
        
        try {
            PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-result.jaif");
            reimTransformer.printJaif(reimOut);
            reimTransformer.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

		
        long endTime   = System.currentTimeMillis();
        System.out.println("INFO: Total running time: " + ((float)(endTime - startTime) / 1000) + " sec");
        
	}
	
	
}
