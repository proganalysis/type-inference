package LinearRegression;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPublicKey;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.math.BigInteger;

import static LinearRegression.Constants.NUM_DELIM;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
// @SuppressWarnings("Duplicates")

public class thetaREDUCE extends Reducer<Text, ObjectWritable, Text, ObjectWritable>{
	public void reduce(Text key, Iterable<Object> values, Context context) throws IOException, InterruptedException{
		String bundle[] = context.getConfiguration().getStrings("bundle");
		boolean USE_ENC = context.getConfiguration().getBoolean("USE_ENC", true);
		if(USE_ENC) {
			String remote_host = bundle[0];
			int remote_port = Integer.parseInt(bundle[1]);
			String public_key = bundle[2];
			PaillierPublicKey pub_key = new PaillierPublicKey(new BigInteger(public_key));
			CryptoWorker cryptoWorker = new CryptoWorker(pub_key, remote_host, remote_port);
			EncryptedNumber sum = cryptoWorker.get_zero();
			float count = 0.0F;
			for (Object value : values) {
				EncryptedNumber value_enc = (EncryptedNumber)value;
				sum = sum.add(value_enc);
				count++;
			}
			EncryptedNumber ans = sum.divide(count);
			String out = String.format("%s%s%d", ans.calculateCiphertext().toString(), NUM_DELIM, ans.getExponent());
			cryptoWorker.send_remote_msg(String.format("ANS: %s", out));
			context.write(key, new ObjectWritable(out));
		}
		else {
			float sum = 0;
			int count = 0;
			for (Object value : values) {
				sum += (double)value;
				count++;
			}
			context.write(key, new ObjectWritable(sum / count));
		}
	}
}
