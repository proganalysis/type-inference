package brown;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import encryption.AHDecryptor;

public class ServerThread implements Runnable {
	private Socket socket = null;
	private AHDecryptor ah;

	public ServerThread(Socket socket, AHDecryptor ah) {
		this.socket = socket;
		this.ah = ah;
	}

	public void run() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			String total_adRevenue_AH = (String) in.readObject();
			double total_adRevenue = 0;
			synchronized (ah) {
				total_adRevenue = ah.decrypt(total_adRevenue_AH).decodeDouble();
			}
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeDouble(total_adRevenue);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
