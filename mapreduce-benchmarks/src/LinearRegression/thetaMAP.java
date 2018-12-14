package LinearRegression;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPublicKey;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskCounter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import static LinearRegression.Constants.NUM_DELIM;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
// @SuppressWarnings("Duplicates")

public class thetaMAP extends Mapper<LongWritable, Text, Text, Text> {
	private static int count=0;
	private static EncryptedNumber[] Xi_enc=null;
	private static double[] Xi=null;
	private static ArrayList<EncryptedNumber> theta_i_enc= new ArrayList<>();
	private static ArrayList<Double> theta_i= new ArrayList<>();
	private static boolean USE_ENC;
	private CryptoWorker cryptoWorker;
	private double alpha;
	private double number_of_inputs;
	private LogWriter logWriter;

	@Override
	public void setup(Context context) {
		alpha = context.getConfiguration().getFloat(Constants.ALPHA_TAG, 0);
		USE_ENC = context.getConfiguration().getBoolean(Constants.USE_ENC_TAG, true);
		boolean hide_vals = context.getConfiguration().getBoolean(Constants.HIDE_VALS_TAG, true);
		String remote_host = context.getConfiguration().get(Constants.REMOTE_HOSTS_TAG);
		int remote_port = context.getConfiguration().getInt(Constants.REMOTE_PORT_TAG, 44444);
		String public_key = context.getConfiguration().get(Constants.PUB_KEY_TAG);
		number_of_inputs = context.getConfiguration().getInt(Constants.NUM_INPUTS_TAG, 30000);
		PaillierPublicKey pub_key = new PaillierPublicKey(new BigInteger(public_key));
		cryptoWorker = new CryptoWorker(pub_key, alpha, number_of_inputs, remote_host, remote_port, hide_vals);
		System.out.println(String.format("Alpha -> %.2f", alpha));
		System.out.println(String.format("number_inputs -> %.2f", number_of_inputs));
		System.out.println(String.format("hide_vals -> %b", hide_vals));
		System.out.println(String.format("USE_ENC -> %b", USE_ENC));

	}

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		++count;
		EncryptedNumber h_theta_enc = cryptoWorker.get_zero();
		double h_theta = 0.0;
		String[] tok=value.toString().split("\\,");
		System.out.println(String.format("got this line \'%s\'", value));
		if(count==1){
			for(int i=0;i<tok.length;i++){
				String current_theta = context.getConfiguration().get("theta".concat(String.valueOf(i)));
				if(USE_ENC) {
					EncryptedNumber tmp = cryptoWorker.cast_encrypted_number_raw_split(current_theta);
					theta_i_enc.add(tmp);
				}
				else {
					theta_i.add(Double.parseDouble(current_theta));
				}
			}
			if(USE_ENC) {
				Xi_enc = new EncryptedNumber[tok.length];
			}
			else {
				Xi = new double[tok.length];
			}
		}

		if(USE_ENC) {
			for(int i=0;i<Xi_enc.length;i++) {
				if (i == 0) {
					Xi_enc[0] = cryptoWorker.get_one();
				} else {
					Xi_enc[i] = cryptoWorker.cast_encrypted_number_raw_split(tok[i - 1]);
				}
			}

			for(int i=0;i<Xi_enc.length;i++){
				EncryptedNumber tmp_multiply = cryptoWorker.remote_op(Xi_enc[i], theta_i_enc.get(i), Operations.MULTIPLY);
				assert tmp_multiply != null;
				h_theta_enc = cryptoWorker.add_enc(h_theta_enc, tmp_multiply);

			}
			EncryptedNumber Yi_enc= cryptoWorker.cast_encrypted_number_raw_split(tok[tok.length-1]);
			for(int i=0;i<Xi_enc.length;i++){
				// theta_i.add(i,(float) (temp+(alpha/number_inputs)*(Yi-h_theta)*(Xi[i])));
				EncryptedNumber temp = theta_i_enc.remove(i);
				EncryptedNumber yi_minus_htheta = cryptoWorker.subtract_enc(Yi_enc, h_theta_enc);
				EncryptedNumber first_mult = cryptoWorker.remote_op(yi_minus_htheta, cryptoWorker.get_normalizer_enc(),  Operations.MULTIPLY);
				assert first_mult != null;
				EncryptedNumber second_mult = cryptoWorker.remote_op(first_mult, Xi_enc[i], Operations.MULTIPLY);
				assert second_mult != null;
				EncryptedNumber ans = cryptoWorker.add_enc(temp, second_mult);
				theta_i_enc.add(i, ans);
			}
		}
		else {
			for(int i=0;i<Xi.length;i++) {
				if (i == 0) {
					Xi[0] = 1.0;
				} else {
					Xi[i] = Double.parseDouble(tok[i - 1]);
				}
			}
			for(int i=0;i<Xi.length;i++){
				h_theta += Xi[i]*theta_i.get(i);
			}
			double Yi=Double.parseDouble(tok[tok.length-1]);
			for(int i=0;i<Xi.length;i++){
				double temp = theta_i.remove(i);
				// theta_i.add(i, (temp+(alpha/number_of_inputs)*(Yi-h_theta)*(Xi[i])));
				double yi_minus_htheta = Yi - h_theta;
				double first_mult = cryptoWorker.remote_op(yi_minus_htheta, cryptoWorker.get_normalizer(), Operations.MULTIPLY);
				double second_mult = cryptoWorker.remote_op(first_mult, Xi[i], Operations.MULTIPLY);
				double ans = temp + second_mult;
				theta_i.add(i, ans);
			}
//			for(int i=0;i<theta_i.size();i++){
//				double d = theta_i.get(i);
//				String out = Double.toString(d);
//				context.write(new Text("theta"+i), new Text(out));
//				cryptoWorker.send_remote_msg("theta"+i + " " + out);
//			}
		}
	}

	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		if(USE_ENC) {
			for (int i = 0; i < theta_i_enc.size(); i++) {
				String out = String.format("%s%s%d", theta_i_enc.get(i).calculateCiphertext().toString(), NUM_DELIM, theta_i_enc.get(i).getExponent());
				context.write(new Text("theta" + i), new Text(out));
			}
		}
		else {
			for(int i=0;i<theta_i.size();i++){
				double d = theta_i.get(i);
				String out = Double.toString(d);
				context.write(new Text("theta"+i), new Text(out));
			}
		}
	}
}
