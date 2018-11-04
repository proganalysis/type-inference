package LinearRegression;

import com.n1analytics.paillier.EncodedNumber;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPublicKey;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class CryptoWorker {
    private int exp;
    private PaillierContext paillier_context;
    private PaillierPublicKey pub_key;
    private EncryptedNumber zero;
    private EncryptedNumber one;
    private EncryptedNumber normalizer;
    private BigInteger phi;
    private BigInteger lambda;
    private BigInteger phi_lambda;
    private EncryptedNumber phi_enc;
    private EncryptedNumber lambda_enc;
    private EncryptedNumber phi_lambda_enc;
    private float alpha;
    private float number_of_inputs;
    private String host;
    private int port;
    public CryptoWorker(PaillierPublicKey pub_key,
                        int exp, float alpha, float number_inputs,
                        String host, int port) {
        this.host = host;
        this.port = port;
        this.pub_key = pub_key;
        this.paillier_context = pub_key.createSignedContext();
        this.exp = exp;
        this.zero = new EncryptedNumber(paillier_context, pub_key.raw_encrypt(new BigInteger("0")), exp);
        this.one = new EncryptedNumber(paillier_context, pub_key.raw_encrypt(new BigInteger("100")), exp);
        this.alpha = alpha;
        this.number_of_inputs = number_inputs;
        this.normalizer = new EncryptedNumber(paillier_context, pub_key.raw_encrypt(new BigInteger(Integer.toString((int)((100 * alpha) / number_inputs) ))), exp);
        generate_phi_lambda();
    }
    public void generate_phi_lambda() {
        SecureRandom rand = new SecureRandom();
        byte[] rand_bytes = new byte[4];
        rand.nextBytes(rand_bytes);
        phi = new BigInteger(String.valueOf(ByteBuffer.wrap(rand_bytes).getInt()));
        lambda = new BigInteger( String.valueOf(ByteBuffer.wrap(rand_bytes).getInt()));
        phi_lambda = phi.multiply(lambda);
        phi_enc = new EncryptedNumber(paillier_context, pub_key.raw_encrypt(new BigInteger(String.valueOf(phi))), exp);
        lambda_enc = new EncryptedNumber(paillier_context, pub_key.raw_encrypt(new BigInteger(String.valueOf(lambda))), exp);
        phi_lambda_enc = new EncryptedNumber(paillier_context, pub_key.raw_encrypt(new BigInteger(String.valueOf(phi_lambda))), exp);
    }

    public EncryptedNumber remote_multiply(EncryptedNumber a, EncryptedNumber b) {
        EncryptedNumber to_send_a = a.add(phi_enc);
        EncryptedNumber to_send_b = b.add(lambda_enc);

        BigInteger ciphertext_a = to_send_a.calculateCiphertext();
        BigInteger ciphertext_b = to_send_b.calculateCiphertext();

        try {
            Socket s = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String msg = "MH|" + ciphertext_a.toString() + "|" + ciphertext_b.toString();
            byte[] ptext = msg.getBytes(StandardCharsets.UTF_8);
            out.write(ptext);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String input_str = br.readLine();
            EncryptedNumber aug_ans = create_encrypted_number(new BigInteger(input_str));
            EncryptedNumber first = a.multiply(lambda);
            EncryptedNumber second = b.multiply(phi);
            return aug_ans.subtract(first).subtract(second).subtract(phi_lambda_enc);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public EncryptedNumber multiply(EncryptedNumber a, EncryptedNumber b) {
        return a;
    }
    public  EncryptedNumber add(EncryptedNumber a, EncryptedNumber b) {
        return a.add(b);
    }
    public  EncryptedNumber divide(EncryptedNumber a, EncryptedNumber b) {
        return a;
    }
    public  EncryptedNumber str_to_encrypted_number(String s) {
        return new EncryptedNumber(paillier_context, new BigInteger(s), exp);
    }
    public EncryptedNumber get_zero() {
        return zero;
    }
    public EncryptedNumber get_one() {
        return one;
    }
    public EncryptedNumber get_normalizer() {
        return normalizer;
    }
    public EncryptedNumber create_encrypted_number(BigInteger s) {
        return new EncryptedNumber(paillier_context, s, exp);
    }
    public EncodedNumber encode_number(BigInteger s) {
        return paillier_context.encode(s);
    }

}
