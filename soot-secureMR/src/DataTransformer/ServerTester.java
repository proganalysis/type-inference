package DataTransformer;

import com.n1analytics.paillier.EncodedNumber;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ServerTester {
     // 66811329142549379802763801401523408106
     // 101704977830150409587518279075343392982
     // 257579400339829463948877597286538000975
     // 137218242983223821440807366692994626504
     // 157410806109755238978603770670695931969
    public static void main(String[] args) {
        BigInteger p = new BigInteger("4115582333");
        BigInteger q = new BigInteger("4013819549");
        BigInteger mod = new BigInteger("16519204823714427817");
        BigInteger pminus1 = p.subtract(new BigInteger("1"));
        BigInteger qminus1 = q.subtract(new BigInteger("1"));
        BigInteger totient = pminus1.multiply(qminus1);

        PaillierPublicKey pub = new PaillierPublicKey(mod);
        PaillierPrivateKey pvt = new PaillierPrivateKey(pub, totient);

        LinearRegression.CryptoWorker cryptoWorker = new LinearRegression.CryptoWorker(pub, 1, 1, 1, "localhost", 44444);
        EncryptedNumber a = cryptoWorker.create_encrypted_number(new BigInteger("66811329142549379802763801401523408106"));
        EncryptedNumber b = cryptoWorker.create_encrypted_number(new BigInteger("101704977830150409587518279075343392982"));

        EncryptedNumber ans = cryptoWorker.remote_multiply(a, b);

        System.out.println(pvt.decrypt(ans).getValue().toString());

    }
}
