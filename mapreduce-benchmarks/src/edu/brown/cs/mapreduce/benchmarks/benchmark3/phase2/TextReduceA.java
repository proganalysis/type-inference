/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPublicKey;

import edu.brown.cs.mapreduce.BenchmarkBase;
import encryption.Util;

/**
 * @author pavlo
 *
 */
public class TextReduceA extends BaseReduce implements Reducer<Text, Text, Text, Text> {
	
	private PaillierContext context;
	private PaillierPublicKey pub;
	private EncryptedNumber zero;
	private String hostName;

	@Override
	public void configure(JobConf conf) {
		String pubKey = conf.get("pubKey");
		pub = new PaillierPublicKey(new BigInteger(pubKey));
		context = pub.createSignedContext();
		zero = context.encrypt(0);
		String[] hostNames = conf.get("hostnames").split(",");
        String taskId = conf.get("mapred.task.id");
        String taskIdSub = taskId.substring(0, taskId.lastIndexOf('_'));
        long taskNumber = Long.parseLong(taskIdSub.substring(taskIdSub.lastIndexOf('_') + 1));
        hostName = hostNames[(int) (taskNumber % hostNames.length)];
	}
	
	public void reduce(Text key,
			   Iterator<Text> values,
			   OutputCollector<Text, Text> output,
			   Reporter reporter) throws IOException {
		if (this.output_text == null) this.output_text = output;
		
		EncryptedNumber total_adRevenue = zero;
		Long counter = 0l;
		EncryptedNumber total_pageRank = zero;
		while (values.hasNext()) {
			Text value = values.next();
			String fields[] = pattern.split(value.toString());
			//
			// <sourceIP> -> (<pageURL>, <pageRank>, <adRevenue>)
			//
			if (fields.length == 3) {
				total_adRevenue = context.add(total_adRevenue, Util.getAHCipher(fields[2], context));
				total_pageRank = context.add(total_pageRank, Util.getAHCipher(fields[1], context));
				//total_adRevenue += Double.parseDouble(fields[2]);
				//total_pageRank += Long.parseLong(fields[1]);
				counter++;
			//
			// Bad mojo!
			//
			} else {
				System.err.println("ERROR: Unexpected value '" + value + "' for key '" + key + "' in Phase2Reduce");
			}
		} // WHILE
		
		Double total_adRevenue_clear = 0.0;
		try {
			Socket socket = new Socket(hostName, 44444);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(Util.getAHString(total_adRevenue));
			out.flush();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			total_adRevenue_clear = in.readDouble();
			socket.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
		}
		
		if (this.max_total_adRevenue == null || total_adRevenue_clear > this.max_total_adRevenue) {
			this.max_total_adRevenue = total_adRevenue_clear;
			//Long average_pageRank = total_pageRank / (long)counter;
			EncryptedNumber average_pageRank = total_pageRank.divide(counter);
			this.max_val = new Text(total_adRevenue_clear.toString() + BenchmarkBase.VALUE_DELIMITER + Util.getAHString(average_pageRank));
			this.max_key = key;
		}
	}
}
