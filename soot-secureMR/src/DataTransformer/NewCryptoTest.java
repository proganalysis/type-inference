package DataTransformer;

import com.n1analytics.paillier.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class NewCryptoTest {

    private static int get_rand_int() {
        SecureRandom rand = new SecureRandom();
        byte[] rand_bytes = new byte[4];
        rand.nextBytes(rand_bytes);
        return ByteBuffer.wrap(rand_bytes).getInt();
    }

    public static void main(String[] args) {

        BigInteger p = new BigInteger("4115582333");
        BigInteger q = new BigInteger("4013819549");
        BigInteger mod = new BigInteger("16519204823714427817");
        BigInteger pminus1 = p.subtract(new BigInteger("1"));
        BigInteger qminus1 = q.subtract(new BigInteger("1"));
        BigInteger totient = pminus1.multiply(qminus1);

        PaillierPublicKey pub = new PaillierPublicKey(mod);
        PaillierPrivateKey pvt = new PaillierPrivateKey(pub, totient);

        PaillierContext context = pub.createSignedContext();

        BigInteger a_int = new BigInteger("500");
        BigInteger b_int = new BigInteger("300");


        BigInteger phi = new BigInteger(String.valueOf(get_rand_int()));
        BigInteger lambda = new BigInteger(String.valueOf(get_rand_int()));
        BigInteger phi_lambda = phi.multiply(lambda);


        EncryptedNumber a = new EncryptedNumber(context, pub.raw_encrypt(a_int), 1);
        EncryptedNumber b = new EncryptedNumber(context, pub.raw_encrypt(b_int), 1);

        EncryptedNumber phi_enc = new EncryptedNumber(context, pub.raw_encrypt(phi), 1);
        EncryptedNumber lambda_enc = new EncryptedNumber(context, pub.raw_encrypt(lambda), 1);
        EncryptedNumber phi_lambda_enc = new EncryptedNumber(context, pub.raw_encrypt(phi_lambda), 1);

        EncryptedNumber to_send_a = a.add(phi_enc);
        EncryptedNumber to_send_b = b.add(lambda_enc);

        // server side

        BigInteger a_decrypted = pvt.decrypt(to_send_a).getValue();
        BigInteger b_decrypted = pvt.decrypt(to_send_b).getValue();
        BigInteger new_num = a_decrypted.multiply(b_decrypted);
        EncryptedNumber to_send = new EncryptedNumber(context, pub.raw_encrypt(new_num), 1);

        // back to client

        EncryptedNumber first = a.multiply(lambda);
        EncryptedNumber second = b.multiply(phi);
        EncryptedNumber ans = to_send.subtract(first).subtract(second).subtract(phi_lambda_enc);
        EncodedNumber c_encoded = pvt.decrypt(ans);
        System.out.println(c_encoded.getValue().toString());;


    }
}
