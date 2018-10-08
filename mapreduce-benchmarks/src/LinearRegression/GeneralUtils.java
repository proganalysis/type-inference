package LinearRegression;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtils {
    private static boolean DEBUG = true;

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

}
