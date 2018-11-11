package DataTransformer;

import com.n1analytics.paillier.EncodedNumber;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;

public class ServerTester {
     // 66811329142549379802763801401523408106
     // 101704977830150409587518279075343392982
     // 257579400339829463948877597286538000975
     // 137218242983223821440807366692994626504
     // 157410806109755238978603770670695931969

    private static double check_neg(double n) {
        return n < 0.0D ? n * -1.0D : n;
    }

    public static void main(String[] args) {
        SecureRandom rand = new SecureRandom();
        byte[] rand_bytes = new byte[2];
        rand.nextBytes(rand_bytes);

        ArrayList<Double> theta_i= new ArrayList<>();

        theta_i.add(0.0);

        Double d = 19d;
        double x = theta_i.get(0);

        BigInteger p = new BigInteger("4115582333");
        BigInteger q = new BigInteger("4013819549");
        BigInteger mod = new BigInteger("16519204823714427817");
        BigInteger pminus1 = p.subtract(new BigInteger("1"));
        BigInteger qminus1 = q.subtract(new BigInteger("1"));
        BigInteger totient = pminus1.multiply(qminus1);
        BigDecimal phi = new BigDecimal(String.valueOf(check_neg(ByteBuffer.wrap(rand_bytes).getChar())));
        rand.nextBytes(rand_bytes);
        BigDecimal lambda = new BigDecimal(String.valueOf(check_neg(ByteBuffer.wrap(rand_bytes).getChar())));
        PaillierPublicKey pub = new PaillierPublicKey(mod);
        PaillierPrivateKey pvt = new PaillierPrivateKey(pub, totient);
        // 6.0 244583953774185486190241972727137617476#-13,
        // 2.2 175594740065577971742773310443803127578#-13,
        LinearRegression.CryptoWorker cryptoWorker = new LinearRegression.CryptoWorker(pub, "localhost", 44444);
        EncryptedNumber a = cryptoWorker.cast_encrypted_number(new BigInteger("244583953774185486190241972727137617476"), -13);
        EncryptedNumber b = cryptoWorker.cast_encrypted_number(new BigInteger("175594740065577971742773310443803127578"), -13);
        cryptoWorker.send_value("a", a);
        cryptoWorker.send_value("b", b);
         EncryptedNumber ans = cryptoWorker.remote_multiply(a, b);
        // double z = cryptoWorker.remote_multiply(5.3, 2.5, "rawr");
        // System.out.println(z);
        System.out.println(pvt.decrypt(ans).decodeDouble());

    }
}
