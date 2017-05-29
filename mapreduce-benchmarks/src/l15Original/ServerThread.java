package l15Original;

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
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			String val = null;
			boolean flag = true;
			while ((val = (String) in.readObject()) != null) {
				if (flag) {
					Double clear;
					synchronized (ah) {
						clear = ah.decrypt(val).decodeDouble();
					}
					out.writeObject(clear.toString());
					flag = false;
				} else {
					int clear2;
					synchronized (ah) {
						clear2 = (int) ah.decrypt(val).decodeLong();
					}
					out.writeInt(clear2);
					out.flush();
					flag = true;
				}
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
