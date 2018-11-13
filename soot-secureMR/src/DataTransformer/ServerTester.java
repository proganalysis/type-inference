package DataTransformer;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;

import java.math.BigInteger;

public class ServerTester {
     // 66811329142549379802763801401523408106
     // 101704977830150409587518279075343392982
     // 257579400339829463948877597286538000975
     // 137218242983223821440807366692994626504
     // 157410806109755238978603770670695931969

    private static double check_neg(double n) {
        return n < 0.0D ? n * -1.0D : n;
    }

    private static void p_enc_num(EncryptedNumber a, PaillierPrivateKey pvt) {
        System.out.println(pvt.decrypt(a).decodeDouble() + " " + a.getExponent());
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
        PaillierContext paillier_context = pub.createSignedContext();
        CryptoWorker cryptoWorker = new CryptoWorker(pub, "localhost", 44444);
        EncryptedNumber a = cryptoWorker.create_encrypted_number(10.0d);
        EncryptedNumber b = cryptoWorker.create_encrypted_number(5.0d);
        EncryptedNumber ans = cryptoWorker.remote_op(a, b, Operations.MULTIPLY);
        p_enc_num(ans, pvt);

    }
}
