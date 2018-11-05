package LinearRegression;

import java.io.IOException;
import java.math.BigInteger;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPublicKey;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
// @SuppressWarnings("Duplicates")

public class thetaREDUCE extends Reducer<Text, ObjectWritable, Text, ObjectWritable>{
	public void reduce(Text key, Iterable<EncryptedNumber> values, Context context) throws IOException, InterruptedException{
		String bundle[] = context.getConfiguration().getStrings("bundle");
		String remote_host=bundle[0];
		int remote_port=Integer.parseInt(bundle[1]);
		String public_key=bundle[2];
		PaillierPublicKey pub_key = new PaillierPublicKey(new BigInteger(public_key));
		CryptoWorker cryptoWorker = new CryptoWorker(pub_key, 1, remote_host, remote_port);
		EncryptedNumber sum = cryptoWorker.get_zero();
        float count = 0.0F;
		for(EncryptedNumber value:values){
			sum = cryptoWorker.add(sum, value);
			count++;
		}
		context.write(key, new ObjectWritable(sum.divide(count)));
	}
}
