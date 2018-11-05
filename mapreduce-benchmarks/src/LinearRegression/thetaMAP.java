package LinearRegression;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.n1analytics.paillier.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
// @SuppressWarnings("Duplicates")

public class thetaMAP extends Mapper<LongWritable, Text, Text, ObjectWritable> {
	private static int count=0;
	private static long number_of_inputs=(long) 0;
	private static float alpha=0.0f;
	private static EncryptedNumber[] Xi=null;
	private static ArrayList<EncryptedNumber> theta_i= new ArrayList<>();
	private static String remote_host;
	private static String public_key;
	private static int remote_port;
	private CryptoWorker cryptoWorker;
	public static LinearRegression.LogWriter logWriter;

	@Override
	public void setup(Context context) {
		// TODO: add this as parameter
		String bundle[] = context.getConfiguration().getStrings("bundle");
		alpha=context.getConfiguration().getFloat("alpha",0);
		remote_host=bundle[0];
		remote_port=Integer.parseInt(bundle[1]);
		public_key=bundle[2];
		number_of_inputs=Long.parseLong(bundle[3]);
		try {
			logWriter = new LogWriter(remote_host, LogWriterType.CONSOLEWRITER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// logWriter.write_net(String.format("Alpha -> %.2f", alpha));
		// logWriter.write_net(String.format("number_inputs -> %d", number_inputs));
		PaillierPublicKey pub_key = new PaillierPublicKey(new BigInteger(public_key));
		cryptoWorker = new CryptoWorker(pub_key, 1, alpha, number_of_inputs, remote_host, remote_port);

	}

	public void map(LongWritable key, Text value, Context context) {
		// TODO: add generate_phi_lambda() call
		++count;
		EncryptedNumber h_theta = cryptoWorker.get_zero();
		String[] tok=value.toString().split("\\,");

		logWriter.write_console(String.format("got this line \'%s\'", value));
		// logWriter.write_net(String.format("current count value -> %d", count));
		if(count==1){
			for(int i=0;i<tok.length;i++){
				String current_theta = context.getConfiguration().get("theta".concat(String.valueOf(i)));
				theta_i.add(cryptoWorker.str_to_encrypted_number(current_theta));
			}
			Xi=new EncryptedNumber[tok.length];
		}
		for(int i=0;i<Xi.length;i++){
			if(i==0){
				Xi[0] = cryptoWorker.get_one();
			}
			else{
				Xi[i] = cryptoWorker.str_to_encrypted_number(tok[i-1]);
		    }
		}
		for(int i=0;i<Xi.length;i++){
			// h_theta += Xi[i]*theta_i.get(i);
			EncryptedNumber tmp_multiply = cryptoWorker.remote_multiply(Xi[i], theta_i.get(i));
            h_theta = cryptoWorker.add(h_theta, tmp_multiply);
		}
		// logWriter.write_net(String.format("current h_theta value -> %.2f", h_theta));
        EncryptedNumber Yi= cryptoWorker.create_encrypted_number(new BigInteger(tok[tok.length-1]));
		// logWriter.write_net(String.format("current Yi value -> %.2f", Yi));
//		StringBuilder sb = new StringBuilder();
//		for(float f : theta_i) {
//			sb.append(Float.toString(f));
//			sb.append(" ");
//		}
		// $f0:$f1:Yi:h_theta:$r12:temp:temp2
		// logWriter.write_net(String.format("theta_i before: %s", sb.toString()));
		// sb = new StringBuilder();
		for(int i=0;i<Xi.length;i++){
			// float temp = theta_i.remove(i);
			// float temp2 = (temp+(normalizer)*(Yi-h_theta)*(Xi[i]));
			// theta_i.add(i, temp2);

			EncryptedNumber temp = theta_i.remove(i);
			EncryptedNumber yi_minus_htheta = cryptoWorker.subtract(Yi, h_theta);
			EncryptedNumber first_mult = cryptoWorker.remote_multiply(cryptoWorker.get_normalizer(), yi_minus_htheta);
			EncryptedNumber second_mult = cryptoWorker.remote_multiply(first_mult, Xi[i]);
			EncryptedNumber ans = cryptoWorker.add(temp, second_mult);
			theta_i.add(i, ans);
		}
//		for(float f : theta_i) {
//			sb.append(Float.toString(f));
//			sb.append(" ");
//		}
		// logWriter.write_net(String.format("theta_i after: %s", sb.toString()));

	}
	// TODO: fix error
	//	Error: java.io.IOException: Can't write: com.n1analytics.paillier.EncryptedNumber@34e520e4 as class com.n1analytics.paillier.EncryptedNumber
	//	at org.apache.hadoop.io.ObjectWritable.writeObject(ObjectWritable.java:208)
	//	at org.apache.hadoop.io.ObjectWritable.writeObject(ObjectWritable.java:128)
	//	at org.apache.hadoop.io.ObjectWritable.write(ObjectWritable.java:82)
	//	at org.apache.hadoop.io.serializer.WritableSerialization$WritableSerializer.serialize(WritableSerialization.java:98)
	//	at org.apache.hadoop.io.serializer.WritableSerialization$WritableSerializer.serialize(WritableSerialization.java:82)
	//	at org.apache.hadoop.mapred.MapTask$MapOutputBuffer.collect(MapTask.java:1157)
	//	at org.apache.hadoop.mapred.MapTask$NewOutputCollector.write(MapTask.java:715)
	//	at org.apache.hadoop.mapreduce.task.TaskInputOutputContextImpl.write(TaskInputOutputContextImpl.java:89)
	//	at org.apache.hadoop.mapreduce.lib.map.WrappedMapper$Context.write(WrappedMapper.java:112)
	//	at LinearRegression.thetaMAP.cleanup(thetaMAP.java:115)
	//	at org.apache.hadoop.mapreduce.Mapper.run(Mapper.java:149)
	//	at org.apache.hadoop.mapred.MapTask.runNewMapper(MapTask.java:787)
	//	at org.apache.hadoop.mapred.MapTask.run(MapTask.java:341)
	//	at org.apache.hadoop.mapred.YarnChild$2.run(YarnChild.java:175)
	//	at java.security.AccessController.doPrivileged(Native Method)
	//	at javax.security.auth.Subject.doAs(Subject.java:422)
	//	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1840)
	//	at org.apache.hadoop.mapred.YarnChild.main(YarnChild.java:169)


	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		for(int i=0;i<theta_i.size();i++){
		    context.write(new Text("theta"+i), new ObjectWritable(theta_i.get(i)));
		}
	}
}
