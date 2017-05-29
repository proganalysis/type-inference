package l6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import encryption.AHEncryptor;
import encryption.DETEncryptor;
import encryption.RNDEncryptor;

public class L6Encryptor {

	static void printUsage() {
		System.out.println("Usage: java L6Encryptor <input folder> <output folder>");
		System.exit(1);
	}

	public static StringJoiner encryptMap(String map, RNDEncryptor rnd) {
		List<String> kvps = Library.splitLine(map, ''); // ^C
		StringJoiner sj = new StringJoiner("");
		for (String potential : kvps) {
			// Split potential on ^D
			List<String> kv = Library.splitLine(potential, ''); // ^D
			sj.add(kv.get(0) + '' + rnd.encrypt(kv.get(1))); // ^D
		}
		return sj;
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
			encryptPageViews(file, bufferedSize, outputFolder, det, ah, rnd);
		}
	}

	private static void encryptPageViews(File file, int bufferedSize, File outputFolder, DETEncryptor det,
			AHEncryptor ah, RNDEncryptor rnd) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file), bufferedSize);
			File outFile = new File(outputFolder.getAbsolutePath() + File.separator + file.getName() + "Cipher");
			outFile.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile), bufferedSize);
			String line = null;
			while ((line = in.readLine()) != null) {
				StringBuffer outline = new StringBuffer();
				List<String> fields = Library.splitLine(line, '');
				String user = fields.get(0);
				outline.append((user.isEmpty() ? "" : det.encrypt(user)) + '');
				outline.append(rnd.encrypt(Integer.parseInt(fields.get(1))) + '');
				outline.append(ah.encrypt(Integer.parseInt(fields.get(2))) + '');
				String query = fields.get(3);
				outline.append((query.isEmpty() ? "" : det.encrypt(query)) + '');
				for (int i = 4; i < 6; i++) {
					long field = Long.parseLong(fields.get(i));
					outline.append(det.encrypt(field) + '');
				}
				String revStr = fields.get(6);
				if (!revStr.isEmpty()) {
					double revenue = Double.parseDouble(revStr);
					outline.append(rnd.encrypt(revenue));
				}
				outline.append(''); // ^A
				outline.append(encryptMap(fields.get(7), rnd));
				outline.append(''); // ^A
				String mapBag = fields.get(8);
				List<String> maps = Library.splitLine(mapBag, ''); // ^B
				StringJoiner sj = new StringJoiner(""); // ^B
				for (String map : maps) {
					StringJoiner enMap = encryptMap(map, rnd);
					sj.add(enMap.toString());
				}
				outline.append(sj);
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
