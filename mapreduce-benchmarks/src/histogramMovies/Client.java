package histogramMovies;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import encryption.AHDecryptor;

public class Client {
	
	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.out.println("ERROR: Wrong number of parameters.");
			System.out.println("Usage: java Client <private key file> <host name> <port number>");
		}
		
	    int portNumber = Integer.parseInt(args[2]);
	    String hostName = args[1];
	    while (true) {
		try {
			Socket socket = new Socket(hostName, portNumber);
			AHDecryptor ah = new AHDecryptor(args[0]);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			String sumRatingStr = (String) in.readObject(); // AH String
			int sumRatings = (int) ah.decrypt(sumRatingStr).decodeLong();
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
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeFloat(outValue);
			out.flush();
			socket.close();
		} catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	}

}
