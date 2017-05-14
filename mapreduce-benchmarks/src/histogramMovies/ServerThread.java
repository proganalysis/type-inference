package histogramMovies;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import encryption.AHDecryptor;

public class ServerThread implements Runnable {
	private Socket socket = null;
	private AHDecryptor ah;
	private int totalNumOfLines;

	public ServerThread(Socket socket, AHDecryptor ah, int totalNumOfLines) {
		this.socket = socket;
		this.ah = ah;
		this.totalNumOfLines = totalNumOfLines;
	}
	
	public void run() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			float[] outValues = new float[totalNumOfLines];
			for (int j = 0; j < totalNumOfLines; j++) {
				String sumRatingStr = (String) in.readObject(); // AH String
				int sumRatings;
				synchronized (ah) {
					sumRatings = (int) ah.decrypt(sumRatingStr).decodeLong();
				}
				int totalReviews = in.readInt();
				float avgReview = (float) sumRatings / (float) totalReviews;
				float absReview = (float) Math.floor((double) avgReview);
				float fraction = avgReview - absReview;
				float division = 0.5f;
				int limitInt = Math.round(1.0f / division);
				float outValue = 0.0f;
				for (int i = 1; i <= limitInt; i++) {
					if (fraction < (division * i)) {
						outValue = absReview + division * i;
						break;
					}
				}
				outValues[j] = outValue;
			}
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			for (float outValue : outValues)
				out.writeFloat(outValue);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
