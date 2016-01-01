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


public class SootObjectImmutability {

	private static void tackTransformer(String name, InferenceTransformer transformer, String[] args) {
		
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

        boolean needTrace = !(System.getProperty("noTrace") != null);

        System.out.println("INFO: Solving "+name+" constraints:  " + transformer.getConstraints().size() + " in total...");
        ConstraintSolver cs = new SetbasedSolver(transformer, false);
        Set<Constraint> errors = cs.solve();
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
        for (Constraint c : errors)
            System.out.println(c);
        System.out.println("INFO: Finish solving "+name+" constraints. " + errors.size() + " error(s)");

        try {
           PrintStream transOut = new PrintStream(outputDir + File.separator + name+"-result.jaif");
	
           transformer.printJaif(transOut); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
                        
	}
	
	
	public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
		        


        set(LEVEL_DEBUG);
          
        InferenceTransformer plainTransformer = new PlainTransformer();
        
        tackTransformer("plain",plainTransformer,args);
        
        ReimUtils.findAllocLhs(plainTransformer);
        //ReimUtils.collectLeakedThisData((LeakTransformer) plainTransformer);
                
        plainTransformer.clear();        
        
        soot.G.v().reset();
        

        set(LEVEL_DEBUG);
                        
        InferenceTransformer reim2Transformer = new ReimTransformer2();
        
        tackTransformer("reim2",reim2Transformer,args);
        
        // ReimUtils.getAllocSiteData(reim2Transformer);

        // reim2Transformer.clear();
        
        soot.G.v().reset();
        
        set(LEVEL_DEBUG);                        

        
        InferenceTransformer leakTransformer = new LeakTransformer();
        
        tackTransformer("leak",leakTransformer,args);
        
        ReimUtils.collectLeakedThisData((LeakTransformer) leakTransformer);

        ReimUtils.getAllocSiteData(reim2Transformer);
        
        // leakTransformer.clear();

        // System.out.println("INFO: Annotated value size: " + AnnotatedValueMap.v().size());
		
        long endTime   = System.currentTimeMillis();
        System.out.println("INFO: Total running time: " + ((float)(endTime - startTime) / 1000) + " sec");
	}
}
