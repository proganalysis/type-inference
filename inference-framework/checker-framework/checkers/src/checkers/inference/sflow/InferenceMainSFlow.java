/**
 * 
 */
package checkers.inference.sflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Comparator;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference.Constraint;
import checkers.inference.ConstraintManager;
import checkers.inference.ConstraintSolver;
import checkers.inference.InferenceChecker;
import checkers.inference.InferenceMain;
import checkers.inference.InferenceUtils;
import checkers.inference.MaximalTypingExtractor;
import checkers.inference.Reference;
import checkers.inference.SetbasedSolver;
import checkers.inference.TypingExtractor;
import checkers.util.CheckerMain;

import com.sun.tools.javac.main.Main;

/**
 * Those helper methods are from {@link CheckerMain}
 * @author huangw5
 *
 */
public class InferenceMainSFlow extends InferenceMain {

	public TypingExtractor getCurrentExtractor() {
		return currentExtractor;
	}

	public ConstraintManager getConstraintManager() {
		return constraintManager;
	}

	public InferenceChecker getInferenceChcker() {
		return inferenceChecker;
	}

	public void setInferenceChcker(InferenceChecker inferenceChcker) {
		this.inferenceChecker = inferenceChcker;
	}

	public List<Reference> infer(String[] args, String jdkBootPaths, PrintWriter out) {
		for (String path : jdkBootPaths.split(File.pathSeparator)) {
			if (!path.equals("") && !new File(path).exists()) 
				throw new RuntimeException("Cannot find the boot jar: " + path);
		}
		
		System.setProperty("sun.boot.class.path",
				jdkBootPaths + ":" + System.getProperty("sun.boot.class.path"));
		
		List<String> argList = new ArrayList<String>(args.length + 10);
        String reimCheckerPath = "checkers.inference.reim.ReimChecker";
        String reimFlowCheckerPath = "";
        int processorIndex = -1;
        for (int i = 0; i < args.length; i++) {
        	String arg = args[i];
        	// Intercept the processor and replace it with ReimChecker
        	if (arg.equals("-processor")) {
        		argList.add(arg);
        		argList.add(reimCheckerPath);	
				processorIndex = ++i;
				reimFlowCheckerPath = args[processorIndex];
        	}
        	else
	        	argList.add(arg);
        }
        // Add arguments
        argList.add("-Xbootclasspath/p:" + jdkBootPaths);
        argList.add("-proc:only");
        argList.add("-Awarns");
        
        
        // Now we run ReIm
        System.out.println("INFO: Running ReIm...");
		com.sun.tools.javac.main.Main main = new com.sun.tools.javac.main.Main("javac", out);
        if (main.compile(argList.toArray(new String[0])) != Main.Result.OK)
        	return null;
        List<Constraint> constraints = constraintManager.getConstraints();
		System.out.println("INFO: Generated " + constraints.size() + " constraints in total");
		if (constraints.isEmpty()) {
			System.out.println("WARN: No constraints generated.");
			return null;
		}
//		if (InferenceChecker.DEBUG) {
//			PrintWriter pw;
//			try {
//				pw = new PrintWriter(InferenceMainSFlow.outputDir
//						+ File.separator + "tf-constraints.log");
//                for (Constraint c : constraints) {
//                    pw.println(c.toString());
//                }
//				pw.close();
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		ConstraintSolver solver = new SetbasedSolver(inferenceChecker, Reference.getExpReferences(), constraints);
		List<Constraint> conflictConstraints = solver.solve();
		currentExtractor = new MaximalTypingExtractor(inferenceChecker, Reference.getExpReferences(), constraints);
		currentExtractor.extractConcreteTyping(0);
		// We need to store the maximal solution for ReIm
		Map<String, Reference> reimMaxTyping = currentExtractor.getInferredSolution();
		PrintWriter pw;
		try {
			pw = new PrintWriter(outputDir + File.separator + "reim-result.csv");
			currentExtractor.printAllVariables(pw);
			pw.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		constraintManager.reset();
		Reference.clearup();
		System.out.println("INFO: Finish ReIm");
		
		// Now we run SFlow
		// First we need to revert the processor
		argList.set(processorIndex, reimFlowCheckerPath);
		main = new com.sun.tools.javac.main.Main("javac", out);
        if (main.compile(argList.toArray(new String[0])) != Main.Result.OK)
        	return null;
        constraints = constraintManager.getConstraints();
		System.out.println("INFO: Generated " + constraints.size() + " constraints in total");
		if (constraints.isEmpty()) {
			System.out.println("WARN: No constraints generated.");
			return null;
		}
//		solver = new SecretFirstSetbasedSolver(inferenceChecker, Reference.getExpReferences(), constraints);
//		solver = new SetbasedSolver(inferenceChecker, Reference.getExpReferences(), constraints);
		solver = new WorklistSetbasedSolver(inferenceChecker, Reference.getExpReferences(), constraints);
		// FIXME: output constraints
		if (InferenceChecker.DEBUG) {
			try {
				pw = new PrintWriter(InferenceMainSFlow.outputDir
						+ File.separator + "tf-constraints.log");
                for (Constraint c : constraints) {
                    pw.println(c.toString());
                }
				pw.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		conflictConstraints = solver.solve();
		if (!conflictConstraints.isEmpty()) {
//			System.out.println("There are " + conflictConstraints.size() + " unresolvable conflicts before resolution: ");
//			for (Constraint c : conflictConstraints) {
//				System.out.println(c);
//			}
//			return Collections.emptyList();
			// FIXME: Now we treat conflict resolution similarly to libraries
//			if (inferenceChecker instanceof SFlowChecker
//					&& !((SFlowChecker) inferenceChecker).isInferLibrary()) {
//				((SFlowChecker) inferenceChecker).setInferLibrary(true);
//			}
		}
		currentExtractor = new MaximalTypingExtractor(inferenceChecker, Reference.getExpReferences(), constraints);
		List<Constraint> typeErrors = currentExtractor.extractConcreteTyping(conflictConstraints.size());
		boolean beforeResolve = false;
		if (typeErrors.isEmpty()) {
			typeErrors = conflictConstraints;
			beforeResolve = true;
		}
        // Sort typeErrors
        Collections.sort(typeErrors, new Comparator<Constraint>() {
            @Override
            public int compare(Constraint o1, Constraint o2) {
                return o1.getID() - o2.getID();
            }
        });
		try {
			pw = new PrintWriter(InferenceMain.outputDir
					+ File.separator + "type-errors.txt");
			String s = "There are " + typeErrors.size() + " type errors "
					+ (beforeResolve ? "BEFORE" : "AFTER") + " resolution:";
			System.out.println(s);
			pw.println(s + "\n");
			int count = 0;
			Constraint pre = null;
			for (Constraint c : typeErrors) {
				if (pre == null || !c.isSimilar(pre)) {
					count++;
//					System.out.println("\n" + count + ": ");
					pw.println("\n" + count + ": ");
				}
//				System.out.println(c);
				pw.println(c);
				pre = c;
			}
			System.out.println("\nThere are " + count + " errors after merging similar ones");
			pw.println("\nThere are " + count + " errors after merging similar ones");
			pw.close();
		} catch (Exception e) {
		}
//		} 
		
		// Now we merge the maximal typing for SFlow and ReIm 
		Map<String, Reference> reimFlowMaxTyping = currentExtractor.getInferredSolution();
		for (Entry<String, Reference> entry : reimMaxTyping.entrySet()) {
			String id = entry.getKey();
			Reference reimRef = entry.getValue();
			Reference reimFlowRef = reimFlowMaxTyping.get(id);
			if (reimFlowRef != null) {
				reimFlowRef.mergeAnnotations(reimRef);
			}
		}
		Reference.clearup();
		return currentExtractor.getInferredReferences();
	}
	
//    public boolean check(String[] args, String jdkBootPaths, PrintWriter out) {
//		System.setProperty("sun.boot.class.path",
//				jdkBootPaths + ":" + System.getProperty("sun.boot.class.path"));
//		List<String> argList = new ArrayList<String>(args.length + 10);
//		argList = new ArrayList<String>(args.length + 10);
//        for (String arg : args) 
//        	argList.add(arg);
//
//        argList.add("-Xbootclasspath/p:" + jdkBootPaths);
//        argList.add("-Achecking");
//        argList.add("-Awarns");
//        argList.add("-proc:only");
//        com.sun.tools.javac.main.Main main = new com.sun.tools.javac.main.Main("javac", out);
//        if (main.compile(argList.toArray(new String[0])) != Main.Result.OK) {
//        	return false;
//        }
//        System.out.println("INFO: Skip checking");
//        return true;
//    }
}
