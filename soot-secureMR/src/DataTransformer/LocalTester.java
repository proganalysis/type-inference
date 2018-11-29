package DataTransformer;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalTester {

    public static void p_enc_num(EncryptedNumber a, PaillierPrivateKey pvt) {
        System.out.println(pvt.decrypt(a).decodeDouble() + " " + a.getExponent());
    }

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static int check_neg(int n) {
        return n < 0 ? n * -1 : n;
    }


//    private static String get_host(ArrayList<String> host_list) {
//        SecureRandom rand = new SecureRandom();
//        byte[] rand_bytes = new byte[4];
//        rand.nextBytes(rand_bytes);
//        int index = check_neg(ByteBuffer.wrap(rand_bytes).getChar());
//        index = index % host_list.size();
//        return host_list.get(index);
//    }

    private static ArrayList<String> make_host_list(String hosts_raw) {
        ArrayList<String> ret_val = new ArrayList<>();
        Pattern ip_regex = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
        for(String s : hosts_raw.split(",")) {
            Matcher matcher = ip_regex.matcher(s);
            if(matcher.matches()) {

                ret_val.add(s);
            }
        }
        return ret_val;
    }

    private static int get_host_index(int list_size) {
        SecureRandom rand = new SecureRandom();
        byte[] rand_bytes = new byte[4];
        rand.nextBytes(rand_bytes);
        int index = check_neg(ByteBuffer.wrap(rand_bytes).getChar());
        return index % list_size;
    }

    private static String get_host(ArrayList<String> a) {
        return a.get(get_host_index(a.size()));
    }

    public static void main(String[] args) {

        System.load("C:/Users/fire/Desktop/gmp.dll");

        PaillierPrivateKey aa = PaillierPrivateKey.create(304);
        PaillierPublicKey bb = aa.getPublicKey();

        String fff = "35.153.53.33,100.24.70.147,54.164.166.51,34.227.72.119";
        ArrayList<String> ffff = make_host_list(fff);

        for(int i =0; i < 10; i++) {
            System.out.println(get_host(ffff));
        }


        // 16642224343773146887
        // 15167667500820634847
        // 252423725320413976638768024298321771289
        BigInteger p = new BigInteger("5342570036094752898706501166998269527820496793");
        BigInteger q = new BigInteger("3825794672921306529743589451290764541402836819");
        BigInteger mod = new BigInteger("20439575983800297986894428086305914391044691441360301033400706265638514798500457944091821467");
        BigInteger pminus1 = p.subtract(new BigInteger("1"));
        BigInteger qminus1 = q.subtract(new BigInteger("1"));
        BigInteger totient = pminus1.multiply(qminus1);

        PaillierPublicKey pub = new PaillierPublicKey(mod);
        PaillierPrivateKey pvt = new PaillierPrivateKey(pub, totient);
        pvt = PaillierPrivateKey.create(512);
        pub = pvt.getPublicKey();

        PaillierContext paillier_context = pub.createSignedContext();
        CryptoWorker cryptoWorker = new CryptoWorker(pub,"localhost", 44444, false);

        int x = (5+(6/3)*(10-1)*(7));
        System.out.println(x);

        Random rand = new Random();


        ArrayList<String> ret_val = new ArrayList<>();
        Pattern ip_regex = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
        for(String s : "82.111.103.1,82.111.103.2,82.111.103.3,82.111.103.4, ,   ,dwad,".split(",")) {
            Matcher matcher = ip_regex.matcher(s);
            if(matcher.matches()) {
                ret_val.add(s);
            }
        }

        System.out.println(get_host(ret_val));
        System.out.println(get_host(ret_val));
        System.out.println(get_host(ret_val));
        System.out.println(get_host(ret_val));
        System.out.println(get_host(ret_val));
        System.out.println(get_host(ret_val));



        EncryptedNumber a = cryptoWorker.create_encrypted_number(10.0);
        EncryptedNumber b = cryptoWorker.create_encrypted_number(5.0);
        EncryptedNumber c = a.subtract(b);//cryptoWorker.local_mult(a, b);

        EncryptedNumber yi = cryptoWorker.create_encrypted_number(0.01);
        p_enc_num(yi, pvt);
        EncryptedNumber h_theta = cryptoWorker.create_encrypted_number(0.13);
        p_enc_num(h_theta, pvt);
        double h_theta_raw = round(pvt.decrypt(h_theta).decodeDouble());//0.130233;
        EncryptedNumber h_theta2 = cryptoWorker.create_encrypted_number(h_theta_raw);
        p_enc_num(h_theta2, pvt);
        p_enc_num(h_theta, pvt);
        p_enc_num(yi, pvt);
        yi = yi.subtract(h_theta2);
        p_enc_num(yi, pvt);
        p_enc_num(h_theta, pvt);

    }
}
