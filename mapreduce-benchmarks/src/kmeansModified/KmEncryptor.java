package kmeansModified;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import encryption.AHEncryptor;
import encryption.DETEncryptor;
import encryption.RNDEncryptor;

public class KmEncryptor {

	static void printUsage() {
		System.out.println("Usage: java KmEncryptor <input folder> <output folder>");
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
			outputFolder.mkdirs();

		AHEncryptor ah = new AHEncryptor(args[1]);
		RNDEncryptor rnd = new RNDEncryptor();
		DETEncryptor det = new DETEncryptor();
		int bufferedSize = 1024 * 1024;
		for (File file : inputFolder.listFiles()) {
			System.out.println("Encrypting file " + file.getName() + "...");
			if (file.getName().startsWith("initial"))
				encryptInit(outputFolder, det, bufferedSize, file);
			else
				encryptInput(outputFolder, ah, rnd, det, bufferedSize, file);
		}
	}

	private static void encryptInit(File outputFolder, DETEncryptor det,
			int bufferedSize, File file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file), bufferedSize);
			File outFile = new File(outputFolder.getAbsolutePath() + File.separator + file.getName() + "Cipher");
			outFile.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile), bufferedSize);
			String line = null;
			while ((line = in.readLine()) != null) {
				StringBuffer outline = new StringBuffer();
				int index = line.lastIndexOf(' ') + 1;
				outline.append(line.substring(0, index));
				String reviews = line.substring(index);
				StringTokenizer token = new StringTokenizer(reviews, ",");
				while (token.hasMoreTokens()) {
					String tok = token.nextToken();
					int reviewIndex = tok.indexOf("_");
					int reviewId = Integer.parseInt(tok.substring(0, reviewIndex));
					outline.append(det.encrypt(reviewId) + "_");
					outline.append(tok.substring(reviewIndex + 1) + ",");
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

	private static void encryptInput(File outputFolder, AHEncryptor ah, RNDEncryptor rnd, DETEncryptor det,
			int bufferedSize, File file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file), bufferedSize);
			File outFile = new File(outputFolder.getAbsolutePath() + File.separator + file.getName() + "Cipher");
			outFile.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile), bufferedSize);
			String line = null;
			while ((line = in.readLine()) != null) {
				StringBuffer outline = new StringBuffer();
				int movieIndex = line.indexOf(":");
				String movieIdStr = line.substring(0, movieIndex);
				long movieId = Long.parseLong(movieIdStr);
				outline.append(rnd.encrypt(movieId) + ":");
				String reviews = line.substring(movieIndex + 1);
				StringTokenizer token = new StringTokenizer(reviews, ",");
				while (token.hasMoreTokens()) {
					String tok = token.nextToken();
					int reviewIndex = tok.indexOf("_");
					int reviewId = Integer.parseInt(tok.substring(0, reviewIndex));
					outline.append(det.encrypt(reviewId) + "_");
					String ratingStr = tok.substring(reviewIndex + 1);
					outline.append(ah.encrypt(ratingStr) + "&");
					int rating = Integer.parseInt(ratingStr);
					outline.append(ah.encrypt(rating * rating) + ",");
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
