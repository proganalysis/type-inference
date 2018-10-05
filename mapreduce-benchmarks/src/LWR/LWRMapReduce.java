package LWR;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.Iterator;

public class LWRMapReduce extends Configured implements Tool {


    public static class MapClass extends MapReduceBase
            implements Mapper<LongWritable, Text, FloatWritable, IntWritable> {

        private INDArray x_query = Nd4j.create(new float[]{1.0f, -5.6f, 9.1566f, -2.0867f},new int[]{1,4});



        INDArray gauss_kernal(INDArray x_to, float c) {
            INDArray d = x_query.sub(x_to);
            INDArray d_sq = d.mmul(d.transpose());
            float divisor = -2.0f * (float)Math.pow(c, 2);
            INDArray tmp = d_sq.div(divisor);
            return tmp; // TODO: need to do e^x_ij for every value in the matrix!
        }

        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<FloatWritable, IntWritable> outputCollector, Reporter reporter) throws IOException {

        }
    }

    public static class Reduce extends MapReduceBase
            implements Reducer<FloatWritable, IntWritable, FloatWritable, IntWritable> {

        @Override
        public void reduce(FloatWritable floatWritable, Iterator<IntWritable> iterator, OutputCollector<FloatWritable, IntWritable> outputCollector, Reporter reporter) throws IOException {

        }
    }

    @Override
    public int run(String[] strings) throws Exception {
        return 0;
    }

    public static void main(String[] args) throws Exception {


        int ret = ToolRunner.run(new LWRMapReduce(), args);
        System.exit(ret);
    }
}
