package kmeansModified;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import encryption.AHDecryptor;

public class Server {
	
	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.out.println("ERROR: Wrong number of parameters.");
			System.out.println("Usage: java Server <private key file> <totalNumOfLines> <port number>");
		}
		
	    int portNumber = Integer.parseInt(args[2]);
	    int totalNumOfLines = Integer.parseInt(args[1]);
		ExecutorService executor = Executors.newFixedThreadPool(50);
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(portNumber);
		    AHDecryptor ah = new AHDecryptor(args[0]);
			while (true) {
				Runnable worker = new ServerThread(serverSocket.accept(), ah, totalNumOfLines);
	            executor.execute(worker);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}

}
