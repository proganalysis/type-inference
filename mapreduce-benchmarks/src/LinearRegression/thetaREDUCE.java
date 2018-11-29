package LinearRegression;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPublicKey;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.math.BigInteger;

import static LinearRegression.Constants.NUM_DELIM;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
// @SuppressWarnings("Duplicates")

public class thetaREDUCE extends Reducer<Text, Text, Text, Text>{
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
		System.out.println("Started a reducer task");
		boolean USE_ENC = context.getConfiguration().getBoolean("USE_ENC", true);
		int remote_port = context.getConfiguration().getInt("remote_port", 44444);
		String public_key = context.getConfiguration().get("public_key");
		String remote_host = context.getConfiguration().get("remote_hosts");
		if(USE_ENC) {
			PaillierPublicKey pub_key = new PaillierPublicKey(new BigInteger(public_key));
			CryptoWorker cryptoWorker = new CryptoWorker(pub_key, remote_host, remote_port);
			EncryptedNumber sum = cryptoWorker.get_zero();
			double count = 0.0;
			for (Text value : values) {
				String raw_vals = value.toString();
				System.out.println(String.format("got value: %s", raw_vals));
				EncryptedNumber value_enc = cryptoWorker.cast_encrypted_number_raw_split(raw_vals);
				sum = sum.add(value_enc);
				count++;
			}
			EncryptedNumber ans = sum.divide(count);
			String out = String.format("%s%s%d", ans.calculateCiphertext().toString(), NUM_DELIM, ans.getExponent());
			cryptoWorker.send_final_value(key.toString(), ans);
			String sb = String.valueOf(key) + "__final";
			Text final_txt = new Text(sb);
			context.write(final_txt, new Text(out));
		}
		else {
			float sum = 0;
			int count = 0;
			for (Text value : values) {
				sum += Double.parseDouble(value.toString());
				count++;
			}
			context.write(key, new Text(Float.toString(sum / count)));
		}
	}
}
