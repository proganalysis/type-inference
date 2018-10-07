package LinearRegression;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtils {
    private static boolean DEBUG = false;

    private static String make_date_msg(String msg) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return String.format("[%s]: %s", formatter.format(date), msg);
    }

    public static void print_msg(String msg) {
        if(DEBUG) {
            System.out.println(make_date_msg(msg));
        }
    }

    public static void send_to_server(String msg) {
        if(DEBUG) {
            try {
                Socket s = new Socket("34.207.147.212", 44444);
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                byte[] ptext = make_date_msg(msg).getBytes("UTF-8");
                out.write(ptext);
                out.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
