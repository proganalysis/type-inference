package edu.rpi;

import soot.PackManager;
import soot.SourceLocator;
import soot.Transform;
import static com.esotericsoftware.minlog.Log.*;

public class TranslatorMain {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		set(LEVEL_DEBUG);

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
			System.err.println("Usage: java edu.rpi.TranslatorMain [-cp CLASSPATH] [-d OUTPUTDIR] MAIN_CLASS");
			System.exit(1);
		}
		
		String[] sootArgs = {
				"-cp", classPath,
				"-app", "-src-prec", "java",
				"-f", "J", mainClass,
				"-d", outputDir
		};
		
		TranslatorTransformer transformer = new TranslatorTransformer(outputDir);
		PackManager.v().getPack("jtp").add(new Transform("jtp.translator", transformer));

		soot.Main.main(sootArgs);

		info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
		info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024 * 1024)));

		long endTime = System.currentTimeMillis();
		System.out.println("INFO: Total running time: " + ((float) (endTime - startTime) / 1000) + " sec");
	}

}
