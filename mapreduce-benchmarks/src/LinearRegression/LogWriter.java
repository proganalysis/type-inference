package LinearRegression;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
    private String remoteAddr;
    private  String make_date_msg(String msg, String hostname) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return String.format("[%s][%s]: %s\n", formatter.format(date), hostname, msg);
    }

    public LogWriter(String remoteAddr) throws IOException {
        this.remoteAddr = remoteAddr;
    }

    public void write_net(String msg) {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            Socket s = new Socket(this.remoteAddr, 44444);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            byte[] ptext = make_date_msg(msg, hostname).getBytes("UTF-8");
            out.write(ptext);
            out.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
