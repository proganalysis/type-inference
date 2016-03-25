package edu.rpi;

import java.util.List;
import java.util.Set;

import java.io.PrintStream;
import java.io.File;

import soot.Main;
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
		// JCryptTranslator translator = new
		// JCryptTranslator(jcryptTransformer);
		PackManager.v().getPack("jtp").add(new Transform("jtp.reim", reimTransformer));
		PackManager.v().getPack("jtp").add(new Transform("jtp.jcrypt", jcryptTransformer));
		// PackManager.v().getPack("jtp").add(new Transform("jtp.translator",
		// translator));

		Main.main(args);

		info(String.format("%6s: %14d", "size", AnnotatedValueMap.v().size()));
		info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
		info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024 * 1024)));

		String outputDir = SourceLocator.v().getOutputDir();

		//System.out.println(
		//		"INFO: Solving Reim constraints:  " + reimTransformer.getConstraints().size() + " in total...");
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
		//System.out.println("INFO: Finish solving Reim constraints. " + errors.size() + " error(s)");

		try {
			PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-result.jaif");
			reimTransformer.printJaif(reimOut);
			// reimTransformer.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//System.out.println(
		//		"INFO: Solving JCrypt constraints:  " + jcryptTransformer.getConstraints().size() + " in total...");
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
		//System.out.println("INFO: Finish solving JCrypt constraints. " + errors.size() + " error(s)");
		info(jcryptTransformer.getName(), "Extracting a concete typing...");
		TypingExtractor extractor = new MaximalTypingExtractor(jcryptTransformer, jcryptSolver);
		List<Constraint> typeErrors = extractor.extract();
		info(jcryptTransformer.getName(), "Finish extracting typing.");
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

		// JCryptTranslator translator = new
		// JCryptTranslator((JCryptTransformer) jcryptTransformer);
		// translator.processFields();
		// for (SootClass sClass : translator.getTranslatedClasses().values()) {
		// String fileName = SourceLocator.v().getFileNameFor(sClass,
		// Options.output_format_jimple);
		// OutputStream streamOut;
		// try {
		// streamOut = new FileOutputStream(fileName);
		// PrintWriter writerOut = new PrintWriter(new
		// OutputStreamWriter(streamOut));
		// Printer.v().printTo(sClass, writerOut);
		// writerOut.flush();
		// streamOut.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }

		System.out.println("INFO: Annotated value size: " + AnnotatedValueMap.v().size());
//		JCryptTranslator translator = new JCryptTranslator(jcryptTransformer);
//		PackManager.v().getPack("jtp").add(new Transform("jtp.translator", translator));
//		int len = args.length;
//		String[] newargs = new String[len - 2];
//		int j = 0;
//		for (int i = 0; i < len; i++) {
//			if (args[i].equals("-cp")) {
//				i++;
//			} else {
//				newargs[j++] = args[i];
//			}
//		}
//		soot.Main.main(newargs);
		
		//main = new G().soot_Main();
		//main.run(newargs);

		long endTime = System.currentTimeMillis();
		System.out.println("INFO: Total running time: " + ((float) (endTime - startTime) / 1000) + " sec");
	}

}
