package edu.rpi;

import soot.PackManager;
import soot.SourceLocator;
import soot.Transform;
import static com.esotericsoftware.minlog.Log.*;

public class TranslatorMain {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		set(LEVEL_DEBUG);

		String outputDir = SourceLocator.v().getOutputDir();

		/* ------------------- OPTIONS ---------------------- */
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-d")) {
				outputDir = args[i + 1];
				break;
			}
		}

		TranslatorTransformer transformer = new TranslatorTransformer(outputDir);
		PackManager.v().getPack("jtp").add(new Transform("jtp.translator", transformer));

		soot.Main.main(args);

		info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
		info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024 * 1024)));

		long endTime = System.currentTimeMillis();
		System.out.println("INFO: Total running time: " + ((float) (endTime - startTime) / 1000) + " sec");
	}

}
