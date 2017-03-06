package edu.rpi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.io.PrintStream;
import java.io.File;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Transform;
import soot.options.Options;
import vasco.soot.examples.AETransformer;
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

		String outputDir = SourceLocator.v().getOutputDir();
		String classPath = "";

		/* ------------------- OPTIONS ---------------------- */
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-d")) {
				outputDir = args[i + 1];
			} else if (args[i].equals("-cp")) {
				classPath = args[i + 1];
			}
		}

		soot.Main.main(args);

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
		extractor.extract();

		try {
			PrintStream jcryptOut = new PrintStream(outputDir + File.separator + "jcrypt-result.jaif");
			jcryptTransformer.printJaif(jcryptOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<SootMethod> entryPoints = new ArrayList<>(JCryptTransformer.entryPoints);
		G.reset();
		AETransformer aet = new AETransformer(outputDir);
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.aet", aet));
		String[] sootArgs = { 
				"-cp", classPath, "-w",
				"-keep-line-number", "-keep-bytecode-offset",
				"-p", "cg", "implicit-entry:false", "-p", "cg.cha", "enabled",
				"-p", "cg.cha", "apponly:true",
				"-p", "cg", "safe-forname", "-p", "cg", "safe-newinstance",
				"-process-dir", outputDir,
				"-f", "none", "-d", outputDir };
		Options.v().parse(sootArgs);
		List<SootMethod> entry = new ArrayList<>();
		for (SootMethod sm : entryPoints) {
			String mainClass = sm.getDeclaringClass().getName();
			SootClass c = Scene.v().forceResolve(mainClass, SootClass.BODIES);
			c.setApplicationClass();
			entry.add(c.getMethod(sm.getName(), sm.getParameterTypes()));
		}
		Scene.v().loadNecessaryClasses();
		Scene.v().setEntryPoints(entry);
		PackManager.v().runPacks();
		
		Set<String> polyValues = ((JCryptTransformer) jcryptTransformer).getPolyValues();

		G.reset();
		AECheckerTransformer aect = new AECheckerTransformer(aet.getAeResults(), polyValues, jcryptTransformer);
		PackManager.v().getPack("jtp").add(new Transform("jtp.aect", aect));
		sootArgs = new String[]{ "-cp", classPath, "-process-dir", outputDir, "-f", "none", "-d", outputDir };
		soot.Main.main(sootArgs);
		Set<String> conversions = aect.getConversions();
		System.out.println("There are " + conversions.size() + " conversions.");
		for (String con : conversions)
			System.out.println(con);
		if (conversions.isEmpty()) {
			System.out.println(aet.formatResults(aect.getEncryptions()));
			// transform
			G.reset();
			TransformerTransformer trans = new TransformerTransformer((JCryptTransformer) jcryptTransformer, polyValues,
					aect.getEncryptions());
			PackManager.v().getPack("jtp").add(new Transform("jtp.transformer", trans));
			sootArgs = new String[] { "-cp", classPath, "-process-dir", outputDir, "-f", "jimple", "-d",
					outputDir + "/transformed" };
			soot.Main.main(sootArgs);
		}

		long endTime = System.currentTimeMillis();
		info("Total running time: " + ((float) (endTime - startTime) / 1000) + " sec");
	}

}
