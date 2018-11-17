package DataTransformer;

import com.n1analytics.paillier.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.SecureRandom;

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

        EncodedNumber phi;
        EncodedNumber lambda;
        EncodedNumber phi_lambda;

        SecureRandom rand = new SecureRandom();

        BigDecimal tmp_phi = new BigDecimal(Double.toString(rand.nextFloat()));
        BigDecimal tmp_lambda = new BigDecimal(Double.toString(rand.nextFloat()));
        BigDecimal tmp_phi_lambda = tmp_phi.multiply(tmp_lambda);

        tmp_phi = tmp_phi.setScale(PLACES, RoundingMode.HALF_UP);
        tmp_lambda = tmp_lambda.setScale(PLACES, RoundingMode.HALF_UP);
        tmp_phi_lambda = tmp_phi_lambda.setScale(PLACES, RoundingMode.HALF_UP);

        phi_lambda = paillier_context.encode(tmp_phi_lambda);
        phi = paillier_context.encode(tmp_phi);
        lambda = paillier_context.encode(tmp_lambda);

        CryptoWorker cryptoWorker = new CryptoWorker(pub, "localhost", 44444, false);




        EncryptedNumber a = cryptoWorker.create_encrypted_number(0.01166666666666661200);
        EncryptedNumber b = cryptoWorker.create_encrypted_number( 1.00000000000000000000);









        p_enc_num(a, pvt);
        EncryptedNumber ans = cryptoWorker.remote_op(a, b, Operations.MULTIPLY);
        p_enc_num(ans, pvt);
        ans = cryptoWorker.round_value(ans);
        p_enc_num(ans, pvt);
        System.out.println(ans.calculateCiphertext() + " " + ans.getExponent());


        double x = ((((4.7 * 0.33) * 0.33) - 0.33) - 0.33);
        System.out.println(x);

    }
}
