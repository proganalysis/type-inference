package classification;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import encryption.AHDecryptor;

public class ServerNS {
	
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("ERROR: Wrong number of parameters.");
			System.out.println("Usage: java Server <private key file>");
		}
		
	    int portNumber = 44444;
		ExecutorService executor = Executors.newFixedThreadPool(50);
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(portNumber);
		    AHDecryptor ah = new AHDecryptor(args[0]);
			while (true) {
				Runnable worker = new ServerThreadNS(serverSocket.accept(), ah);
	            executor.execute(worker);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}

}
