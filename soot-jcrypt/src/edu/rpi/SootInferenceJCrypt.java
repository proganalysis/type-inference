package edu.rpi;

import java.util.List;
import java.util.Set;

import java.io.PrintStream;
import java.io.File;

import soot.PackManager;
import soot.SourceLocator;
import soot.Transform;
import edu.rpi.jcrypt.*;
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

		String classPath = ".";		
		String mainClass = null;
		String outputDir = SourceLocator.v().getOutputDir();
		
		/* ------------------- OPTIONS ---------------------- */
		try {
			int i=0;
			while(true){
				if (args[i].equals("-cp")) {
					classPath = args[i+1];
					i += 2;
				} else if (args[i].equals("-d")) {
					outputDir = args[i+1];
					i += 2;
				} else {
					mainClass = args[i];
					i++;
					break;
				}
			}
			if (i != args.length || mainClass == null)
				throw new Exception();
		} catch (Exception e) {
			System.err.println("Usage: java edu.rpi.SootInferenceJCrypt [-cp CLASSPATH] [-d OUTPUTDIR] MAIN_CLASS");
			System.exit(1);
		}
		
		String[] sootArgs = {
				"-cp", classPath,
				"-app", "-src-prec", "java",
				"-f", "J", mainClass,
				"-d", outputDir
		};
		
		soot.Main.main(sootArgs);

		info(String.format("%6s: %14d", "size", AnnotatedValueMap.v().size()));
		info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
		info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024 * 1024)));

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

		try {
			PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-result.jaif");
			reimTransformer.printJaif(reimOut);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		TypingExtractor extractor = new MaximalTypingExtractor(jcryptTransformer, jcryptSolver);
		List<Constraint> typeErrors = extractor.extract();
		if (!typeErrors.isEmpty()) {
			for (Constraint c : typeErrors)
				System.out.println(c);
		}

		try {
			PrintStream jcryptOut = new PrintStream(outputDir + File.separator + "jcrypt-result.jaif");
			jcryptTransformer.printJaif(jcryptOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			PrintStream jcryptOut = new PrintStream(outputDir + File.separator + "poly-result.txt");
			jcryptTransformer.printPolyResult(jcryptOut);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("INFO: Annotated value size: " + AnnotatedValueMap.v().size());

		long endTime = System.currentTimeMillis();
		System.out.println("INFO: Total running time: " + ((float) (endTime - startTime) / 1000) + " sec");
	}

}
