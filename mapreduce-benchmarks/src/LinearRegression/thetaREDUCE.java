package LinearRegression;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPublicKey;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.math.BigInteger;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
// @SuppressWarnings("Duplicates")

public class thetaREDUCE extends Reducer<Text, Text, Text, Text>{
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
		LogWriter logWriter = new LogWriter();
		logWriter.write_out("Started a reducer task");
		boolean USE_ENC = context.getConfiguration().getBoolean(Constants.USE_ENC_TAG, true);
		int remote_port = context.getConfiguration().getInt(Constants.REMOTE_PORT_TAG, 44444);
		String public_key = context.getConfiguration().get(Constants.PUB_KEY_TAG);
		String remote_host = context.getConfiguration().get(Constants.REMOTE_HOSTS_TAG);
        PaillierPublicKey pub_key = new PaillierPublicKey(new BigInteger(public_key));
        CryptoWorker cryptoWorker = new CryptoWorker(pub_key, remote_host, remote_port);
		if(USE_ENC) {
			EncryptedNumber sum = cryptoWorker.get_zero();
			double count = 0.0;
			for (Text value : values) {
				String raw_vals = value.toString();
				EncryptedNumber value_enc = cryptoWorker.cast_encrypted_number_raw_split(raw_vals);
				sum = cryptoWorker.add_enc(sum, value_enc);
				count++;
			}
			EncryptedNumber ans = sum.divide(count);
			String out = String.format("%s%s%d", ans.calculateCiphertext().toString(), Constants.NUM_DELIM, ans.getExponent());
			String sb = String.valueOf(key) + "__final";
			Text final_txt = new Text(sb);
			context.write(final_txt, new Text(out));
		}
		else {
			double sum = 0.0;
			int count = 0;
			for (Text value : values) {
				sum += Double.parseDouble(value.toString());
				count++;
			}
			double ans = sum / count;
			cryptoWorker.send_remote_msg(String.format("%s -> sum = %.4f count = %d ans = %f", key, sum, count, ans));
			context.write(key, new Text(Double.toString(ans)));
		}
	}
}
