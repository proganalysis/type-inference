package LinearRegression;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPublicKey;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import static LinearRegression.Constants.NUM_DELIM;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
// @SuppressWarnings("Duplicates")

public class thetaMAP extends Mapper<LongWritable, Text, Text, ObjectWritable> {
	private static int count=0;
	private static double number_of_inputs = 0.0D;
	private static double alpha = 0.0D;
	private static EncryptedNumber[] Xi_enc=null;
	private static double[] Xi=null;
	private static ArrayList<EncryptedNumber> theta_i_enc= new ArrayList<>();
	private static ArrayList<Double> theta_i= new ArrayList<>();
	private static String remote_host;
	private static String public_key;
	private static int remote_port;
	private static boolean USE_ENC;
	private CryptoWorker cryptoWorker;
	public static LinearRegression.LogWriter logWriter;

	@Override
	public void setup(Context context) {

		// TODO: add this as parameterr
		String bundle[] = context.getConfiguration().getStrings("bundle");
		alpha = context.getConfiguration().getFloat("alpha",0);
		USE_ENC = context.getConfiguration().getBoolean("USE_ENC", true);
		remote_host = bundle[0];
		remote_port = Integer.parseInt(bundle[1]);
		public_key = bundle[2];
		number_of_inputs = Long.parseLong(bundle[3]);
		try {
			logWriter = new LogWriter(remote_host, LogWriterType.CONSOLEWRITER);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		long input_num = context.getCounter(TaskCounter.MAP_INPUT_RECORDS).getValue();
//		cryptoWorker.send_remote_msg("INPUT_COUNTER: " + Long.toString(input_num));
		PaillierPublicKey pub_key = new PaillierPublicKey(new BigInteger(public_key));
		cryptoWorker = new CryptoWorker(pub_key, alpha, number_of_inputs, remote_host, remote_port);
		cryptoWorker.send_remote_msg(String.format("Alpha -> %.2f", alpha));
		cryptoWorker.send_remote_msg(String.format("number_inputs -> %.2f", number_of_inputs));

	}

	public void map(LongWritable key, Text value, Context context) {
		// TODO: add generate_phi_lambda() call
		++count;
		EncryptedNumber h_theta_enc = cryptoWorker.get_zero();
		// cryptoWorker.send_value("INIT HTHETA", h_theta_enc);
		double h_theta = 0.0D;
		String[] tok=value.toString().split("\\,");
		// cryptoWorker.send_remote_msg(String.format("got this line \'%s\'", value));
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
				// h_theta += Xi[i]*theta_i.get(i);
				// cryptoWorker.send_value("h_theta_before", h_theta_enc);
				EncryptedNumber tmp_multiply = cryptoWorker.remote_op(Xi_enc[i], theta_i_enc.get(i), "Xi_enc[i], theta_i_enc.get(i)", Operations.MULTIPLY);
				// cryptoWorker.send_value(String.format("Xi[%d]", i), Xi_enc[i]);
				// cryptoWorker.send_value(String.format("theta_i_enc.get(%d)", i), theta_i_enc.get(i));
				assert tmp_multiply != null;
				h_theta_enc = h_theta_enc.add(tmp_multiply);
				// cryptoWorker.send_value("h_theta_after", h_theta_enc);

			}
			EncryptedNumber Yi_enc= cryptoWorker.cast_encrypted_number_raw_split(tok[tok.length-1]);
			for(int i=0;i<Xi_enc.length;i++){
			 	// TODO: javallier library messus up add and subtract on floating point numbers with large amounts of sig figs
				// theta_i.add(i,(float) (temp+(alpha/number_inputs)*(Yi-h_theta)*(Xi[i])));
				EncryptedNumber temp = theta_i_enc.remove(i);
				// cryptoWorker.send_value("Yi_enc", Yi_enc);
				// cryptoWorker.send_value("h_theta_enc", h_theta_enc);
				// EncryptedNumber yi_minus_htheta = cryptoWorker.remote_op(Yi_enc, h_theta_enc, "Yi_enc - h_theta_enc", Operations.SUBTRACT);
				EncryptedNumber yi_minus_htheta = Yi_enc.subtract(h_theta_enc);
				// cryptoWorker.send_value("yi_minus_htheta", yi_minus_htheta);
				EncryptedNumber first_mult = cryptoWorker.remote_op(yi_minus_htheta, cryptoWorker.get_normalizer_enc(), "yi_minus_htheta, cryptoWorker.get_normalizer_enc()", Operations.MULTIPLY);
				assert first_mult != null;
				EncryptedNumber second_mult = cryptoWorker.remote_op(first_mult, Xi_enc[i], "first_mult, Xi_enc[i]", Operations.MULTIPLY);
				assert second_mult != null;
				EncryptedNumber ans = temp.add(second_mult);
				theta_i_enc.add(i, ans);
			}
		}
		else {
			for(int i=0;i<Xi.length;i++) {
				if (i == 0) {
					Xi[0] = 1.0D;
				} else {
					Xi[i] = Double.parseDouble(tok[i - 1]);
				}
			}
			for(int i=0;i<Xi.length;i++){
				// cryptoWorker.send_remote_msg(String.format("h_theta_before = %.5f", h_theta));
				// cryptoWorker.send_remote_msg(String.format("Xi[%d] = %.5f", i, Xi[i]));
				// cryptoWorker.send_remote_msg(String.format("theta_i.get(%d) = %.5f", i, theta_i.get(i)));
				h_theta += Xi[i]*theta_i.get(i);
				// cryptoWorker.send_remote_msg(String.format("h_theta_after = %.5f", h_theta));
			}
			double Yi=Double.parseDouble(tok[tok.length-1]);
			for(int i=0;i<Xi.length;i++){
				double temp = theta_i.remove(i);
				// cryptoWorker.send_remote_msg(String.format("Yi = %.5f", Yi));
				// cryptoWorker.send_remote_msg(String.format("h_theta = %.5f", h_theta));
				double yi_minus_htheta = Yi - h_theta;
				// cryptoWorker.send_remote_msg(String.format("yi_minus_htheta = %.5f", yi_minus_htheta));
				double first_mult = cryptoWorker.remote_op(yi_minus_htheta, cryptoWorker.get_normalizer(), "yi_minus_htheta, cryptoWorker.get_normalizer()", Operations.MULTIPLY);
				double second_mult = cryptoWorker.remote_op(first_mult, Xi[i], "first_mult, Xi[i]", Operations.MULTIPLY);
				double ans = temp + second_mult;
				theta_i.add(i, ans);
			}
		}
	}

	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		if(USE_ENC) {
			for (int i = 0; i < theta_i_enc.size(); i++) {
				String out = String.format("%s%s%d", theta_i_enc.get(i).calculateCiphertext().toString(), NUM_DELIM, theta_i_enc.get(i).getExponent());
				context.write(new Text("theta" + i), new ObjectWritable(out));
			}
		}
		else {
			for(int i=0;i<theta_i.size();i++){
				double d = theta_i.get(i);
				String out = Double.toString(d);
				context.write(new Text("theta"+i), new ObjectWritable(out));
			}
		}
	}
}
