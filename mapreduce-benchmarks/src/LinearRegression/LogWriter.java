package LinearRegression;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
    private String name;
    private InetAddress remoteAddr;
    private DatagramSocket s;
    private LinearRegression.LogWriterType type;
    private boolean REMOTE_DEBUG = false;
    private boolean CONSOLE_DEBUG = true;
    private  String make_date_msg(String msg) {
        // SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SS");
        SimpleDateFormat formatter = new SimpleDateFormat("d/M/y H:m:s:S");
        Date date = new Date();
        return String.format("[%s]: %s\n", formatter.format(date), msg);
    }

    LogWriter(String name, LinearRegression.LogWriterType type) throws IOException {
        this.type = type;
        switch (this.type) {
            case NETWRITER: {
                this.remoteAddr = InetAddress.getByName(name);
                s = new DatagramSocket();
                break;
            }
            case CONSOLEWRITER: {
                this.name = name;
                break;
            }
        }
    }

    void write_net(String msg) {
        if (this.REMOTE_DEBUG && this.type == LinearRegression.LogWriterType.NETWRITER) {
            try {
                byte buf[] = make_date_msg(msg).getBytes(StandardCharsets.UTF_8);
                DatagramPacket p = new DatagramPacket(buf, buf.length, this.remoteAddr, 44444);
                s.send(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write_console(String msg) {
        if(this.CONSOLE_DEBUG && this.type == LinearRegression.LogWriterType.CONSOLEWRITER) {
            System.out.print(make_date_msg(msg));
        }
    }
}
