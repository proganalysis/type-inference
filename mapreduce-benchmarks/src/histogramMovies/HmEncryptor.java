package histogramMovies;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import encryption.AHEncryptor;
import encryption.RNDEncryptor;

public class HmEncryptor {

	static void printUsage() {
		System.out.println("Usage: java HmEncryptor <input folder> <output folder>");
		System.exit(1);
	}

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("ERROR: Wrong number of parameters.");
			printUsage();
		}

		File inputFolder = new File(args[0]);
		File outputFolder = new File(args[1] + "/dir");
		if (outputFolder.exists()) {
			for (File file : outputFolder.listFiles())
				file.delete();
		} else
			outputFolder.mkdir();
		
		AHEncryptor ah = new AHEncryptor(args[1]);
		RNDEncryptor rnd = new RNDEncryptor();
		int bufferedSize = 1024 * 1024;
		for (File file : inputFolder.listFiles()) {
			System.out.println("Encrypting file " + file.getName() + "...");
			try {
				BufferedReader in = new BufferedReader(new FileReader(file), bufferedSize);
				File outFile = new File(outputFolder.getAbsolutePath() + File.separator + file.getName() + "Cipher");
				outFile.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(outFile), bufferedSize);
				String line = null;
				while ((line = in.readLine()) != null) {
					StringBuffer outline = new StringBuffer();
					int movieIndex = line.indexOf(":");
					long movieId = Long.parseLong(line.substring(0, movieIndex));
					outline.append(rnd.encrypt(movieId) + ":");
					String reviews = line.substring(movieIndex + 1);
					StringTokenizer token = new StringTokenizer(reviews, ",");
					while (token.hasMoreTokens()) {
						String tok = token.nextToken();
						int reviewIndex = tok.indexOf("_");
						int reviewId = Integer.parseInt(tok.substring(0, reviewIndex));
						outline.append(rnd.encrypt(reviewId) + "_");
						String ratingStr = tok.substring(reviewIndex + 1);
						outline.append(ah.encrypt(ratingStr) + ",");
					}
					outline.append("\n");
					out.write(outline.toString());
				}
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
