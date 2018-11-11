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
import java.util.Objects;

public class CryptoWorker {
    private static final String NUM_DELIM = "#";
    private static final String MSG_DELIM = "|";
    private PaillierContext paillier_context;
    private PaillierPublicKey pub_key;
    private EncryptedNumber zero;
    private EncryptedNumber one;
    private EncryptedNumber normalizer;
    private BigInteger phi;
    private BigInteger lambda;
    private EncryptedNumber phi_enc;
    private EncryptedNumber lambda_enc;
    private EncryptedNumber phi_lambda_enc;
    private String host;
    private int port;
    public CryptoWorker(PaillierPublicKey pub_key,
                        double alpha, double number_of_inputs,
                        String host, int port) {
        this.host = host;
        this.port = port;
        this.pub_key = pub_key;
        this.paillier_context = pub_key.createSignedContext();
        this.zero = paillier_context.encrypt(0.0D);
        this.one = paillier_context.encrypt(1.0D);
        this.normalizer = paillier_context.encrypt(alpha / number_of_inputs);
        this.send_remote_msg(String.format("NORMALIZER %.6f", alpha / number_of_inputs));
        generate_phi_lambda();
    }

    public CryptoWorker(PaillierPublicKey pub_key,
                        String host, int port) {
        this.host = host;
        this.port = port;
        this.pub_key = pub_key;
        this.paillier_context = pub_key.createSignedContext();
        this.zero = paillier_context.encrypt(0.0D);
        this.one = paillier_context.encrypt(1.0D);
        this.normalizer = paillier_context.encrypt(0.0D);
        generate_phi_lambda();
    }

    private static int check_neg(int n) {
        return n < 0 ? n * -1 : n;
    }

    private void generate_phi_lambda() {
        SecureRandom rand = new SecureRandom();
        byte[] rand_bytes = new byte[4];
        rand.nextBytes(rand_bytes);
        phi = new BigInteger("1000");//String.valueOf(check_neg(ByteBuffer.wrap(rand_bytes).getInt())));
        rand.nextBytes(rand_bytes);
        lambda = new BigInteger("200000");//String.valueOf(check_neg(ByteBuffer.wrap(rand_bytes).getInt())));
        BigInteger phi_lambda = phi.multiply(lambda);
        phi_enc = paillier_context.encrypt(phi);
        lambda_enc = paillier_context.encrypt(lambda);
        phi_lambda_enc = paillier_context.encrypt(phi_lambda);
    }

    private EncryptedNumber send_mult_enc(EncryptedNumber a, EncryptedNumber b, String additional_msg) {
        generate_phi_lambda();

        send_remote_msg(String.format("PHI: %s LAMBDA %s", phi.toString(), lambda.toString()));



        EncryptedNumber to_send_a = a.add(phi_enc);
        EncryptedNumber to_send_b = b.add(lambda_enc);

        send_value("phi_enc", phi_enc);
        send_value("a", a);
        send_value("to_send_a", to_send_a);

        BigInteger ciphertext_a = to_send_a.calculateCiphertext();
        BigInteger ciphertext_b = to_send_b.calculateCiphertext();

        try {
            Socket s = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String msg;
            String msg_format;
            if(Objects.equals(additional_msg, "")) {
                msg_format = "%s%s%s%s%s%s%d%s%s%s%d";
                msg = String.format(msg_format, "OPE", MSG_DELIM, "MH", MSG_DELIM,
                        ciphertext_a.toString(), NUM_DELIM, to_send_a.getExponent(), MSG_DELIM,
                        ciphertext_b.toString(), NUM_DELIM, to_send_b.getExponent());
            }
            else {
                msg_format = "%s%s%s%s%s%s%d%s%s%s%d%s%s";
                msg = String.format(msg_format, "OPE", MSG_DELIM, "MH", MSG_DELIM,
                        ciphertext_a.toString(), NUM_DELIM, to_send_a.getExponent(), MSG_DELIM,
                        ciphertext_b.toString(), NUM_DELIM, to_send_b.getExponent(),
                        MSG_DELIM, additional_msg);
            }
            byte[] ptext = msg.getBytes(StandardCharsets.UTF_8);
            out.write(ptext);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            char[] raw_input = new char[1024];
            int rc = br.read(raw_input);
            if(rc < 0) {
                System.err.println(String.format("ERROR: read %d from read rc", rc));
            }
            String input_str = new String(raw_input).trim();
            if(!Objects.equals(input_str, "ERROR")) {
                EncryptedNumber aug_ans = cast_encrypted_number_raw_split(input_str);
                EncryptedNumber first = a.multiply(lambda);
                EncryptedNumber second = b.multiply(phi);
                s.close();
                return aug_ans.subtract(first).subtract(second).subtract(phi_lambda_enc);
            }
            else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double send_mult_pt(double a, double b, String additional_msg) {
        try {
            Socket s = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String msg = String.format("%s%s%s%s%s%s%s", "OPD",
                    MSG_DELIM, Double.toString(a),
                    MSG_DELIM, Double.toString(b),
                    MSG_DELIM, additional_msg);
            byte[] ptext = msg.getBytes(StandardCharsets.UTF_8);
            out.write(ptext);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            char[] raw_input = new char[1024];
            int rc = br.read(raw_input);
            if(rc < 0) {
                System.err.println(String.format("ERROR: read %d from read rc", rc));
            }
            String input_str = new String(raw_input).trim();
            if(!Objects.equals(input_str, "ERROR")) {
                return Double.parseDouble(input_str);
            }
            else {
                return 0.0D;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0D;
    }

    public EncryptedNumber remote_multiply(EncryptedNumber a, EncryptedNumber b) {
        return this.send_mult_enc(a, b, "");
    }

    public double remote_multiply(double a, double b, String additional_msg) {
        return this.send_mult_pt(a, b, additional_msg);
    }

    public EncryptedNumber remote_multiply_msg(EncryptedNumber a, EncryptedNumber b, String msg) {
        return this.send_mult_enc(a, b, msg);
    }

    public void send_remote_msg(String base_msg) {
        try {
            Socket s = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String msg = String.format("MSG|%s", base_msg);
            byte[] ptext = msg.getBytes(StandardCharsets.UTF_8);
            out.write(ptext);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send_value(String name, EncryptedNumber a){
        try {
            Socket s = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String msg = String.format("VALUE|%s|%s#%d", name, a.calculateCiphertext().toString(), a.getExponent());
            byte[] ptext = msg.getBytes(StandardCharsets.UTF_8);
            out.write(ptext);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public EncryptedNumber subtract(EncryptedNumber a, EncryptedNumber b) {
        return a.subtract(b);
    }
    public EncryptedNumber add(EncryptedNumber a, EncryptedNumber b) {
        return a.add(b);
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
        return this.paillier_context.encrypt(s);
    }
    public EncryptedNumber create_encrypted_number(String s) {
        return paillier_context.encrypt(new BigInteger(s));
    }
    public EncryptedNumber create_encrypted_number(Double d) {
        return paillier_context.encrypt(d);
    }
    public EncryptedNumber cast_encrypted_number(String cipher_text, int exp) {
        return new EncryptedNumber(paillier_context, new BigInteger(cipher_text), exp);
    }
    public EncryptedNumber cast_encrypted_number(BigInteger cipher_text, int exp) {
        return new EncryptedNumber(paillier_context, cipher_text, exp);
    }
    public EncryptedNumber cast_encrypted_number_raw_split(String line) {
        String[] raw_split = line.split(NUM_DELIM);
        return cast_encrypted_number(raw_split[0], Integer.parseInt(raw_split[1]));
    }
    public EncodedNumber encode_number(BigInteger s) {
        return paillier_context.encode(s);
    }

}
