package LinearRegression;

import com.n1analytics.paillier.EncodedNumber;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPublicKey;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Objects;

import static LinearRegression.Constants.MSG_DELIM;
import static LinearRegression.Constants.NUM_DELIM;

public class CryptoWorker {
    private PaillierContext paillier_context;
    private PaillierPublicKey pub_key;
    private EncryptedNumber zero;
    private EncryptedNumber one;
    private EncryptedNumber normalizer_enc;
    private double phi;
    private double lambda;
    private EncryptedNumber phi_enc;
    private EncryptedNumber lambda_enc;
    private EncryptedNumber phi_lambda_enc;
    private String host;
    private int port;
    private double normalizer;
    private static final int PLACES = 2;

    public CryptoWorker(PaillierPublicKey pub_key,
                        double alpha, double number_of_inputs,
                        String host, int port) {
        this.host = host;
        this.port = port;
        this.pub_key = pub_key;
        this.paillier_context = pub_key.createSignedContext();
        this.zero = paillier_context.encrypt(0.0D);
        this.one = paillier_context.encrypt(1.0D);
        this.normalizer = alpha / number_of_inputs;
        this.normalizer_enc = paillier_context.encrypt(normalizer);
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
        this.normalizer = 0.0D;
        this.normalizer_enc = paillier_context.encrypt(normalizer);
        // generate_phi_lambda();
    }

    private static int check_neg(int n) {
        return n < 0 ? n * -1 : n;
    }

    private String get_op_str(Operations op) {
        switch (op) {
            case MULTIPLY:
                return "MH";
            case SUBTRACT:
                return "SUB";
        }
        return "NONE";
    }


    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(PLACES, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private void generate_phi_lambda() {
        // TODO: this is still not working as intended
        SecureRandom rand = new SecureRandom();
        phi = round(rand.nextFloat());
        lambda = round(rand.nextFloat());
        double phi_lambda = phi * lambda;
        phi_enc = paillier_context.encrypt(phi);
        lambda_enc = paillier_context.encrypt(lambda);
        phi_lambda_enc = paillier_context.encrypt(phi_lambda);
    }

    private EncryptedNumber send_op_enc(EncryptedNumber a, EncryptedNumber b, String additional_msg, Operations op) {
        // generate_phi_lambda();

        // send_remote_msg(String.format("PHI: %f LAMBDA %f", phi, lambda));

        // EncryptedNumber to_send_a = a.add(phi);
        // EncryptedNumber to_send_b = b.add(lambda);

        String op_str = get_op_str(op);

        BigInteger ciphertext_a = a.calculateCiphertext();
        BigInteger ciphertext_b = b.calculateCiphertext();

        try {
            Socket s = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String msg;
            String msg_format;
            if(Objects.equals(additional_msg, "")) {
                msg_format = "%s%s%s%s%s%s%d%s%s%s%d";
                msg = String.format(msg_format, "OPE", MSG_DELIM, op_str, MSG_DELIM,
                        ciphertext_a.toString(), NUM_DELIM, a.getExponent(), MSG_DELIM,
                        ciphertext_b.toString(), NUM_DELIM, b.getExponent());
            }
            else {
                msg_format = "%s%s%s%s%s%s%d%s%s%s%d%s%s";
                msg = String.format(msg_format, "OPE", MSG_DELIM, op_str, MSG_DELIM,
                        ciphertext_a.toString(), NUM_DELIM, a.getExponent(), MSG_DELIM,
                        ciphertext_b.toString(), NUM_DELIM, b.getExponent(),
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
                // TODO: this is still not working as intended
                // EncryptedNumber first = a.multiply(lambda);
                // EncryptedNumber second = b.multiply(phi);
                s.close();
                return aug_ans; //.subtract(first).subtract(second).subtract(lambda * phi);
            }
            else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double send_op_pt(double a, double b, String additional_msg, Operations op) {
        String op_str = get_op_str(op);
        try {
            Socket s = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String msg = String.format("%s%s%s%s%s%s%s%s%s", "OPD",
                    MSG_DELIM, op_str,
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


    public EncryptedNumber remote_op(EncryptedNumber a, EncryptedNumber b, Operations op) {
        return this.send_op_enc(a, b, "", op);
    }

    public EncryptedNumber remote_op(EncryptedNumber a, EncryptedNumber b, String additional_msg, Operations op) {
        return this.send_op_enc(a, b, additional_msg, op);
    }

    public double remote_op(double a, double b, String additional_msg, Operations op) {
        return this.send_op_pt(a, b, additional_msg, op);
    }

    public double remote_op(double a, double b, Operations op) {
        return this.send_op_pt(a, b, "", op);
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


    public EncryptedNumber get_zero() {
        return zero;
    }
    public EncryptedNumber get_one() {
        return one;
    }
    public EncryptedNumber get_normalizer_enc() {
        return normalizer_enc;
    }
    public double get_normalizer() {
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
