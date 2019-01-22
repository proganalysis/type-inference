package LinearRegression;

import com.n1analytics.paillier.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTester {
    // 66811329142549379802763801401523408106
    // 101704977830150409587518279075343392982
    // 257579400339829463948877597286538000975
    // 137218242983223821440807366692994626504
    // 157410806109755238978603770670695931969


    private static final int PLACES = 5;
    private static double check_neg(double n) {
        return n < 0.0D ? n * -1.0D : n;
    }

    private static void p_enc_num(EncryptedNumber a, PaillierPrivateKey pvt) {
        System.out.println(pvt.decrypt(a).decodeDouble() + " " + a.getExponent());
    }

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void main(String[] args) {


        BigInteger p = new BigInteger("5342570036094752898706501166998269527820496793");
        BigInteger q = new BigInteger("3825794672921306529743589451290764541402836819");
        BigInteger mod = new BigInteger("20439575983800297986894428086305914391044691441360301033400706265638514798500457944091821467");
        BigInteger pminus1 = p.subtract(new BigInteger("1"));
        BigInteger qminus1 = q.subtract(new BigInteger("1"));
        BigInteger totient = pminus1.multiply(qminus1);

        PaillierPublicKey pub = new PaillierPublicKey(mod);
        PaillierPrivateKey pvt = new PaillierPrivateKey(pub, totient);
        PaillierContext paillier_context = pub.createSignedContext();



        CryptoWorker cryptoWorker = new CryptoWorker(pub, "127.0.0.1", 44444, true);

        double d2 = 0.0;
        double count = 0;

        for(int i = 0; i < 453; i++) {
            d2 += i;
            count++;
        }

        System.out.println(d2 / count);

        // cryptoWorker.send_remote_msg("lalalalala");

        EncryptedNumber a = cryptoWorker.create_encrypted_number(0.01166666666666661200);
        EncryptedNumber b = cryptoWorker.create_encrypted_number( 1.00000000000000000000);

        EncryptedNumber ans = cryptoWorker.remote_op(a, b, "feafes", Operations.MULTIPLY);
        p_enc_num(ans, pvt);
        ans = cryptoWorker.remote_round(ans);
        p_enc_num(ans, pvt);
//        System.out.println(ans.calculateCiphertext() + " " + ans.getExponent());


        double d = cryptoWorker.remote_op(10.0, 20.0, Operations.MULTIPLY);
        double x = ((((4.7 * 0.33) * 0.33) - 0.33) - 0.33);
        System.out.println(x);

        EncryptedNumber e = cryptoWorker.create_encrypted_number(50.0);
        EncryptedNumber f = cryptoWorker.create_encrypted_number( 50.0);

        EncryptedNumber ans2 = cryptoWorker.remote_op(e, f, "feafes", Operations.DIVIDE);
        boolean rawr = cryptoWorker.remote_compare_enc(e, f, ComparisonOperator.GTE, "");
        System.out.println(rawr);

        double u = cryptoWorker.remote_op(20, 20, Operations.MULTIPLY);
        System.out.println(u);

        p_enc_num(ans2, pvt);
        ans2 = cryptoWorker.remote_round(ans2);
        p_enc_num(ans2, pvt);

    }
}
