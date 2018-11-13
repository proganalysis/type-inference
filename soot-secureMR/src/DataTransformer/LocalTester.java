package DataTransformer;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class LocalTester {

    public static void p_enc_num(EncryptedNumber a, PaillierPrivateKey pvt) {
        System.out.println(pvt.decrypt(a).decodeDouble() + " " + a.getExponent());
    }

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void main(String[] args) {
        BigInteger p = new BigInteger("94028421798108658065983939145167508185344846264998716600360366570357521750597");
        BigInteger q = new BigInteger("88747269719464841585520587469635161356202715255126386936579883832478299374653");
        BigInteger mod = new BigInteger("8344765710612356362123652943233543188417167186982505615679396916352063638143504329387287046434809473270740110064040923675389861768599385627649390929417841");
        BigInteger pminus1 = p.subtract(new BigInteger("1"));
        BigInteger qminus1 = q.subtract(new BigInteger("1"));
        BigInteger totient = pminus1.multiply(qminus1);

        PaillierPublicKey pub = new PaillierPublicKey(mod);
        PaillierPrivateKey pvt = new PaillierPrivateKey(pub, totient);
        pvt = PaillierPrivateKey.create(512);
        pub = pvt.getPublicKey();

        PaillierContext paillier_context = pub.createSignedContext();
        CryptoWorker cryptoWorker = new CryptoWorker(pub,"localhost", 44444);

        int x = (5+(6/3)*(10-1)*(7));
        System.out.println(x);

        EncryptedNumber a = cryptoWorker.create_encrypted_number(10.0);
        EncryptedNumber b = cryptoWorker.create_encrypted_number(5.0);
        EncryptedNumber c = a.subtract(b);//cryptoWorker.local_mult(a, b);

        EncryptedNumber yi = cryptoWorker.create_encrypted_number(1.0d);
        p_enc_num(yi, pvt);
        EncryptedNumber h_theta = cryptoWorker.create_encrypted_number(0.130233343243253252);
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
