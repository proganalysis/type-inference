package SVM.io;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

// from here https://github.com/tzulitai/distributed-svm

public class TrainingSubsetRecordReader extends RecordReader<NullWritable, Text> {

	private FileSplit fileSplit;
	private Configuration conf;
	private boolean processed = false;
	private NullWritable key = NullWritable.get();
	private Text value = new Text();
	
	@Override
	public void close() throws IOException {
		// do nothing
	}

	@Override
	public NullWritable getCurrentKey() throws IOException,
			InterruptedException {
		return key;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return processed ? 1.0f : 0.0f;
	}

	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext)
			throws IOException, InterruptedException {
		this.fileSplit = (FileSplit) inputSplit;
		this.conf = taskAttemptContext.getConfiguration();
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if(!processed) {
			byte[] contents = new byte[(int) fileSplit.getLength()];
			
			Path file = fileSplit.getPath();
			FileSystem fs = file.getFileSystem(conf);
			
			FSDataInputStream in = null;
			try {
				in = fs.open(file);
				IOUtils.readFully(in, contents, 0, contents.length);
				value.set(contents, 0, contents.length);
			} finally {
				IOUtils.closeStream(in);
			}
			
			processed = true;
			return true;
		}
		return false;
	}
}
