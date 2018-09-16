package histogramMovies;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import encryption.AHDecryptor;

public class ServerNS {
	public static int requests = 0;
	private static String getDateString() {
		DateFormat df = new SimpleDateFormat("[MM-dd-yyyy HH:mm:ss] ");
		Date today = Calendar.getInstance().getTime();
		return df.format(today);
	}

	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.out.println("ERROR: Wrong number of parameters.");
			System.out.println("Usage: java Server <private key file> <port number>");
		}

	    int portNumber = Integer.parseInt(args[1]);
		ExecutorService executor = Executors.newFixedThreadPool(1000);
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(portNumber);
		    AHDecryptor ah = new AHDecryptor(args[0]);
		    @SuppressWarnings("resource")
			PrintWriter writer = new PrintWriter("log.txt", "UTF-8");
			Timer timer = new Timer();

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					writer.println(getDateString() + "In the last second got " + Integer.toString(requests) + " requests.");
					requests = 0;
				}
			}, 0, 1000);
			while (true) {
				Socket workerSocket = serverSocket.accept();
				requests++;
				//System.out.println(workerSocket.getRemoteSocketAddress().toString());
				writer.println(getDateString() + "Remote ip -> " + workerSocket.getRemoteSocketAddress().toString());
				writer.flush();
				Runnable worker = new ServerThreadNS(workerSocket, ah);
	            executor.execute(worker);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}

}
