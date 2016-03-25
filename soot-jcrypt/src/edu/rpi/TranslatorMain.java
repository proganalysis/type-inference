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

		TranslatorTransformer transformer = new TranslatorTransformer(outputDir);
		PackManager.v().getPack("jtp").add(new Transform("jtp.translator", transformer));

		soot.Main.main(args);

		info(String.format("%6s: %14f MB", "free", ((float) Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
		info(String.format("%6s: %14f MB", "total", ((float) Runtime.getRuntime().totalMemory()) / (1024 * 1024)));

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

		// JCryptTranslator translator = new
		// JCryptTranslator(jcryptTransformer);
		// PackManager.v().getPack("jtp").add(new Transform("jtp.translator",
		// translator));
		// int len = args.length;
		// String[] newargs = new String[len - 2];
		// int j = 0;
		// for (int i = 0; i < len; i++) {
		// if (args[i].equals("-cp")) {
		// i++;
		// } else {
		// newargs[j++] = args[i];
		// }
		// }
		// soot.Main.main(newargs);

		long endTime = System.currentTimeMillis();
		System.out.println("INFO: Total running time: " + ((float) (endTime - startTime) / 1000) + " sec");
	}

}
