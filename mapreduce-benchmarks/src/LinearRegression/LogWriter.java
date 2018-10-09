package LinearRegression;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
    private InetAddress remoteAddr;
    private String hostname;
    private DatagramSocket s;
    private  String make_date_msg(String msg) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return String.format("[%s][%s]: %s\n", formatter.format(date), this.hostname, msg);
    }

    public LogWriter(String remoteAddr) throws IOException {
        this.remoteAddr = InetAddress.getByName(remoteAddr);
        this.hostname = InetAddress.getLocalHost().getHostName();
        s = new DatagramSocket();

    }

    public void write_net(String msg) {
        try {
            byte buf[] = make_date_msg(msg).getBytes(StandardCharsets.UTF_8);
            DatagramPacket p = new DatagramPacket(buf, buf.length,  this.remoteAddr, 44444);
            s.send(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
