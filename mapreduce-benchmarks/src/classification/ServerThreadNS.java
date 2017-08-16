package classification;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import encryption.AHDecryptor;

public class ServerThreadNS implements Runnable {
	private Socket socket = null;
	private AHDecryptor ah;

	public ServerThreadNS(Socket socket, AHDecryptor ah) {
		this.socket = socket;
		this.ah = ah;
	}

	public void run() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			int totalClusters = in.readInt();
			int clusterId = 0;
			float max_similarity = 0.0f;
			for (int p = 0; p < totalClusters; p++) {
				String sq_aCipher = (String) in.readObject();
				int sq_a = (int) ah.decrypt(sq_aCipher).decodeLong();
				int sq_b = in.readInt();
				float denom = (float) ((Math.sqrt((double) sq_a)) * (Math.sqrt((double) sq_b)));
				String numerCipher = (String) in.readObject();
				int numer = (int) ah.decrypt(numerCipher).decodeLong();
				if (denom > 0) {
					float similarity = numer / denom;
					if (similarity > max_similarity) {
						max_similarity = similarity;
						clusterId = p;
					}
				}
			}
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeInt(clusterId);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
