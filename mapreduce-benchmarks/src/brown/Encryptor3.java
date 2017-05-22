package brown;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import encryption.AHEncryptor;
import encryption.DETEncryptor;
import encryption.RNDEncryptor;
import jope.OPE;

public class Encryptor3 {

	static void printUsage() {
		System.out.println("Usage: java Encryptor3 <input folder> <output folder> <startdate> <stopdate>");
		System.exit(1);
	}

	public static void main(String[] args) {

		if (args.length != 4) {
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
		OPE ope = new OPE();
		String opeNumFile = args[1] + "/opeNum";
		try (PrintWriter out = new PrintWriter(opeNumFile)) {
			out.println("startdate-" + args[2] + ": " + ope.encrypt(new BigInteger(args[2])));
			out.println("stopdate-" + args[3] + ": " + ope.encrypt(new BigInteger(args[3])));
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find the location " + opeNumFile);
		}
		int bufferedSize = 1024;
		for (File file : inputFolder.listFiles()) {
			System.out.println("Encrypting file " + file.getName() + "...");
			if (file.getName().equals("rankings"))
				encryptRankings(file, bufferedSize, outputFolder, det, ah, rnd);
			else
				encryptUservisits(file, bufferedSize, outputFolder, det, ah, rnd, ope);
		}
	}
	
	private static void encryptRankings(File file, int bufferedSize, File outputFolder, DETEncryptor det,
			AHEncryptor ah, RNDEncryptor rnd) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file), bufferedSize);
			File outFile = new File(outputFolder.getAbsolutePath() + File.separator + file.getName() + "Cipher");
			outFile.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile), bufferedSize);
			String line = null;
			while ((line = in.readLine()) != null) {
				StringBuffer outline = new StringBuffer();
				int indexTab = line.indexOf(9);
				String pageURL = line.substring(0, indexTab);
				outline.append(det.encrypt(pageURL) + "\t");
				String rest = line.substring(indexTab + 1);
				int indexBar = rest.indexOf('|');
				outline.append(ah.encrypt(rest.substring(0, indexBar)) + '|');
				outline.append(rnd.encrypt(Integer.parseInt(rest.substring(indexBar + 1))));
				outline.append("\n");
				out.write(outline.toString());
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void encryptUservisits(File file, int bufferedSize, File outputFolder, DETEncryptor det,
			AHEncryptor ah, RNDEncryptor rnd, OPE ope) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file), bufferedSize);
			File outFile = new File(outputFolder.getAbsolutePath() + File.separator + file.getName() + "Cipher");
			outFile.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile), bufferedSize);
			String line = null;
			while ((line = in.readLine()) != null) {
				StringBuffer outline = new StringBuffer();
				int indexTab = line.indexOf(9);
				String ip = line.substring(0, indexTab);
				outline.append(det.encrypt(ip) + "\t");
				String[] fields = line.substring(indexTab + 1).split("\\|");
				outline.append(det.encrypt(fields[0]) + "|");
				String date = fields[1].substring(0, 4);
				int indexDash1 = fields[1].indexOf('-'), indexDash2 = fields[1].lastIndexOf('-');
				date += (indexDash2 - indexDash1 == 2 ? "0" : "") + fields[1].substring(indexDash1 + 1, indexDash2);
				date += (fields[1].length() - indexDash2 == 2 ? "0" : "") + fields[1].substring(indexDash2 + 1);
				outline.append(ope.encrypt(new BigInteger(date)) + "|");
				outline.append(ah.encrypt(Float.parseFloat(fields[2])) + "|");
				for (int i = 3; i < 7; i++)
					outline.append(rnd.encrypt(fields[i]) + "|");
				outline.append(rnd.encrypt(Integer.parseInt(fields[7])));
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
