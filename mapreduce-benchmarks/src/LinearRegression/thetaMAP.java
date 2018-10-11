package LinearRegression;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskCounter;
import scala.Int;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
@SuppressWarnings("Duplicates")

public class thetaMAP extends Mapper<LongWritable, Text, Text, FloatWritable> {
	public static int count=0;
	public static long number_inputs=(long) 0;
	public static float alpha=0.0f;
	public static Float[] Xi=null;
	public static ArrayList<Float> theta_i=new ArrayList<Float>();
	public static String remoteAddr;
	public static LinearRegression.LogWriter logWriter;

    // public static final Log log = LogFactory.getLog(thetaMAP.class);
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		String bundle[] = context.getConfiguration().getStrings("bundle");
		alpha=context.getConfiguration().getFloat("alpha",0);
		remoteAddr=bundle[0]; // context.getConfiguration().get("remoteAddr");
		logWriter = new LinearRegression.LogWriter(remoteAddr);
		number_inputs=Long.parseLong(bundle[1]);
        // number_inputs=context.getCounter(org.apache.hadoop.mapred.Task.Counter.MAP_INPUT_RECORDS).getValue();
		// number_inputs=context.getCounter(TaskCounter.MAP_INPUT_RECORDS).getValue();

		logWriter.write_net(String.format("Alpha -> %.2f", alpha));
		logWriter.write_net(String.format("number_inputs -> %d", number_inputs));

	}

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		++count;
		float h_theta=0;
		String[] tok=value.toString().split("\\,");

		logWriter.write_net(String.format("got this line \'%s\'", value));
		logWriter.write_net(String.format("current count value -> %d", count));
		if(count==1){
			for(int i=0;i<tok.length;i++){
				theta_i.add(context.getConfiguration().getFloat("theta".concat(String.valueOf(i)),0));
			}
			Xi=new Float[tok.length];
		}
		for(int i=0;i<Xi.length;i++){
			if(i==0){
				Xi[0]= 1.0f;
			}
			else{
				Xi[i]=Float.parseFloat(tok[i-1]);
		    }
		}
		for(int i=0;i<Xi.length;i++){
			h_theta += Xi[i]*theta_i.get(i);
		}
		logWriter.write_net(String.format("current h_theta value -> %.2f", h_theta));
		float Yi=Float.parseFloat(tok[tok.length-1]);
		logWriter.write_net(String.format("current Yi value -> %.2f", Yi));
		StringBuilder sb = new StringBuilder();
		for(float f : theta_i) {
			sb.append(Float.toString(f));
			sb.append(" ");
		}
		logWriter.write_net(String.format("theta_i before: %s", sb.toString()));
		sb = new StringBuilder();
		for(int i=0;i<Xi.length;i++){
			float temp=theta_i.get(i);
			theta_i.remove(i);
			theta_i.add(i, (temp+(alpha/number_inputs)*(Yi-h_theta)*(Xi[i])));
		}
		for(float f : theta_i) {
			sb.append(Float.toString(f));
			sb.append(" ");
		}
		logWriter.write_net(String.format("theta_i after: %s", sb.toString()));

	}
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		for(int i=0;i<theta_i.size();i++){
		    context.write(new Text("theta"+i), new FloatWritable(theta_i.get(i)));
		}
	}
}
