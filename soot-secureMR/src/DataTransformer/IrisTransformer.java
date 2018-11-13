package DataTransformer;

import com.n1analytics.paillier.*;

import java.io.*;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IrisTransformer {
    public static void main(String[] args) {

        BigInteger p = new BigInteger("94028421798108658065983939145167508185344846264998716600360366570357521750597");
        BigInteger q = new BigInteger("88747269719464841585520587469635161356202715255126386936579883832478299374653");
        BigInteger mod = new BigInteger("8344765710612356362123652943233543188417167186982505615679396916352063638143504329387287046434809473270740110064040923675389861768599385627649390929417841");
        BigInteger pminus1 = p.subtract(new BigInteger("1"));
        BigInteger qminus1 = q.subtract(new BigInteger("1"));
        BigInteger totient = pminus1.multiply(qminus1);

        PaillierPublicKey pub = new PaillierPublicKey(mod);
        PaillierPrivateKey pvt = new PaillierPrivateKey(pub, totient);

        PaillierPublicKey.Serializer pub_serializer = new PaillierPublicKey.Serializer() {
            @Override
            public void serialize(BigInteger bigInteger) {
                System.out.println(bigInteger);
            }
        };

        PaillierPrivateKey.Serializer serializer = new PaillierPrivateKey.Serializer() {
            @Override
            public void serialize(PaillierPublicKey paillierPublicKey, BigInteger bigInteger, BigInteger bigInteger1) {
                System.out.println(bigInteger);
                System.out.println(bigInteger1);
                System.out.println(paillierPublicKey);
            }

        };
        pub.serialize(pub_serializer);
        pvt.serialize(serializer);

//
//        PaillierPrivateKey pvt = PaillierPrivateKey.create(64);
//        PaillierPublicKey pub = pvt.getPublicKey();


        PaillierContext context = pub.createSignedContext();

        BigInteger a_raw = pub.raw_encrypt(new BigInteger("5"));
        BigInteger b_raw = pub.raw_encrypt(new BigInteger("20"));

        EncryptedNumber a = new EncryptedNumber(context, a_raw, 1);
        EncryptedNumber b = new EncryptedNumber(context, b_raw, 1);


        EncryptedNumber c = context.add(a, b);

        EncodedNumber c_encoded = pvt.decrypt(c);
        System.out.println(c_encoded.getValue().toString());

        BigInteger bi = pvt.raw_decrypt(new BigInteger("24455590686746858144017520591119508899"));
        System.out.println(bi);

        String in_file = "generated_iris_data";
        String out_file = "generated_iris_data_out";
        try {
            String line = null;
            FileReader fileReader = new FileReader(in_file);
            FileWriter fileWriter = new FileWriter(out_file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            Pattern myRe = Pattern.compile("^((?:\\d+\\.)?\\d+),((?:\\d+\\.)?\\d+),((?:\\d+\\.)?\\d+),((?:\\d+\\.)?\\d+),((?:\\d+\\.)?\\d+)$");
            int line_num = 1;
            while((line = bufferedReader.readLine()) != null) {
                if(line_num % 10000 == 0) {
                    System.out.println(String.format("Got to line %d", line_num));
                }
                String trimmed_line = line.trim();
                Matcher m = myRe.matcher(trimmed_line);
                if(m.matches()) {
                    StringBuilder new_line = new StringBuilder();
                    for(int i = 1; i < 6; i++) {
                        EncryptedNumber num_enc = context.encrypt(Double.parseDouble(m.group(i)));
                        String line_msg = String.format("%s#%d,", num_enc.calculateCiphertext().toString(), num_enc.getExponent());
                        // System.out.println(m.group(i) + " " + line_msg);
                        new_line.append(line_msg);
                    }

                    new_line.append("\n");
                    bufferedWriter.write(new_line.toString());
                }
                else {
                    System.err.println(String.format("No match for line %d (%s)",line_num, trimmed_line));
                    System.exit(0);
                }
                line_num++;
            }
            bufferedReader.close();
            bufferedWriter.close();
            fileReader.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
