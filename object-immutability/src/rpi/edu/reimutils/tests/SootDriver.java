package rpi.edu.reimutils.tests;

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
import rpi.edu.reimutils.Doop;
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
import edu.rpi.AnnotatedValue;
import edu.rpi.AnnotatedValueMap;
import edu.rpi.Constraint;
import edu.rpi.ConstraintSolver;
import edu.rpi.InferenceTransformer;
import edu.rpi.SetbasedSolver;
import edu.rpi.sflow.*;
import edu.rpi.reim.*;
import edu.rpi.reimutils.CallConstraintGraph;
import edu.rpi.reimutils.ConstraintGraph;
import edu.rpi.reimutils.FieldConstraintGraph;
import edu.rpi.reimutils.SCCUtilities;
import edu.rpi.reimutils.SCCUtilities.*;
import static com.esotericsoftware.minlog.Log.*;


public class SootDriver {
	
	public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
		        
        set(LEVEL_DEBUG);

        InferenceTransformer reimTransformer = new ReimTransformer();

        PackManager.v().getPack("jtp").add(new Transform("jtp.reim", reimTransformer));    
        Scene.v().addBasicClass("java.io.FileFilter",SootClass.SIGNATURES);
        Scene.v().addBasicClass("javax.swing.event.ChangeListener",SootClass.SIGNATURES);
        Scene.v().addBasicClass("javax.swing.event.ListSelectionListener",SootClass.SIGNATURES);
		
        soot.Main.main(args);

        info(String.format("%6s: %14d", "size", AnnotatedValueMap.v().size()));
        info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024*1024)));
        info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024*1024)));

        String outputDir = SourceLocator.v().getOutputDir();

        boolean needTrace = !(System.getProperty("noTrace") != null);

        System.out.println("INFO: Solving Reim constraints:  " + reimTransformer.getConstraints().size() + " in total...");
        ConstraintSolver cs = new SetbasedSolver(reimTransformer, false);
        Set<Constraint> errors = cs.solve();
        try {
            PrintStream leakOut = new PrintStream(outputDir + File.separator + "leak-constraints.log");
            for (Constraint c : reimTransformer.getConstraints()) {
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
	
           reimTransformer.printJaif(reimOut); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        ConstraintGraph cg = new CallConstraintGraph(reimTransformer);
        cg.createNewGraph();
        SCCUtilities.collapseGraph(cg, new CallFilter());
        cg.addLocalEdges();
        SCCUtilities.collapseGraph(cg, new CallFilter());
        cg.addLocalEdges();
        cg.closure();
        //cg.printGraph();
        cg.buildPtGraph();
        //cg.printPtGraph();
        cg.getPtSets(null);
        
        
        
        ConstraintGraph fg = new FieldConstraintGraph(reimTransformer);
        fg.createNewGraph();
        SCCUtilities.collapseGraph(fg, new FieldFilter());
        fg.addLocalEdges();
        SCCUtilities.collapseGraph(fg, new FieldFilter());
        fg.addLocalEdges();
        fg.closure();
        //fg.printGraph();
        fg.buildPtGraph();
        //fg.printPtGraph();
        fg.getPtSets(null);
        
        HashMap<AnnotatedValue,HashSet<AnnotatedValue>> result = cg.getPtSets(fg);
        
        Doop.compare(result);
        
        reimTransformer.clear();        

//      System.out.println("INFO: Annotated value size: " + AnnotatedValueMap.v().size());
		
        long endTime   = System.currentTimeMillis();
        System.out.println("INFO: Total running time: " + ((float)(endTime - startTime) / 1000) + " sec");
	}
}
