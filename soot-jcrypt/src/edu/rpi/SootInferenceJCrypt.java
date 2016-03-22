package edu.rpi;

import java.util.List;
import java.util.Set;

import java.io.PrintStream;
import java.io.File;

import soot.PackManager;
import soot.SourceLocator;
import soot.Transform;
import edu.rpi.jcrypt.*;
import edu.rpi.jcrypt.JCryptTransformer;
import edu.rpi.reim.*;
import static com.esotericsoftware.minlog.Log.*;


public class SootInferenceJCrypt {
	
	public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
		
        set(LEVEL_DEBUG);

        InferenceTransformer reimTransformer = new ReimTransformer();
        InferenceTransformer jcryptTransformer = new JCryptTransformer();
        PackManager.v().getPack("jtp").add(new Transform("jtp.reim", reimTransformer));
        PackManager.v().getPack("jtp").add(new Transform("jtp.jcrypt", jcryptTransformer));

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
            reimOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Constraint c : errors)
            System.out.println(c);
        System.out.println("INFO: Finish solving Reim constraints. " + errors.size() + " error(s)");

        try {
            PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-result.jaif");
            reimTransformer.printJaif(reimOut);
            //reimTransformer.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Solving JCrypt constraints:  " + jcryptTransformer.getConstraints().size() + " in total...");
        ConstraintSolver jcryptSolver = new JCryptConstraintSolver(jcryptTransformer);
        errors = jcryptSolver.solve();
        try {
            PrintStream jcryptOut = new PrintStream(outputDir + File.separator + "jcrypt-constraints.log");
            for (Constraint c : jcryptTransformer.getConstraints()) {
                jcryptOut.println(c);
                jcryptOut.println();
            }
            jcryptOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println();
        for (Constraint c : errors)
            System.out.println(c + "\n");
        System.out.println("INFO: Finish solving JCrypt constraints. " + errors.size() + " error(s)");
        info(jcryptTransformer.getName(), "Extracting a concete typing...");
		TypingExtractor extractor = new MaximalTypingExtractor(jcryptTransformer, jcryptSolver);
		List<Constraint> typeErrors = extractor.extract();
		info(jcryptTransformer.getName(), "Finish extracting typing.");
		if (!typeErrors.isEmpty()) {
			for (Constraint c : typeErrors) System.out.println(c);
		}
		
        try {
            PrintStream jcryptOut = new PrintStream(outputDir + File.separator + "jcrypt-result.jaif");
            jcryptTransformer.printJaif(jcryptOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Annotated value size: " + AnnotatedValueMap.v().size());
        
        JCryptTranslator translator = new JCryptTranslator(jcryptTransformer);
        translator.getPolyMethods();
		
        long endTime   = System.currentTimeMillis();
        System.out.println("INFO: Total running time: " + ((float)(endTime - startTime) / 1000) + " sec");
	}

}
