package LinearRegression;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
    private  String make_date_msg(String msg) {
        SimpleDateFormat formatter = new SimpleDateFormat("d/M/y H:m:s:S");
        Date date = new Date();
        return String.format("[%s]: %s\n", formatter.format(date), msg);
    }

    LogWriter() { }

    public void write_out(String msg) {
        System.out.print(make_date_msg(msg));
    }
    public void write_err(String msg) {
        System.err.print(make_date_msg(msg));
    }
}
