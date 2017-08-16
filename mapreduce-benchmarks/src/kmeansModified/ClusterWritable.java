package kmeansModified;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class ClusterWritable implements Writable{

  public float similarity;
  public ArrayList<Float> similarities;
  public ArrayList<String> movies;

  public ClusterWritable(){
    similarity = 0.0f;
    similarities = new ArrayList<Float>();
    movies = new ArrayList<String>();
  }
  public ClusterWritable(ClusterWritable a){ // copying the cluster
    similarity = a.similarity;
    similarities = new ArrayList<Float>();
    float sim;
    for(int i = 0; i < a.similarities.size(); i++){
      sim = a.similarities.get(i); 
      similarities.add(sim);
    }
    movies = new ArrayList<String>();
    String mv;
    for(int i = 0; i < a.movies.size(); i++){
      mv = new String(a.movies.get(i)); 
      movies.add(mv);
    }
  }
  public void readFields(DataInput in) throws IOException {
    similarity = in.readFloat();
    movies.clear();
    similarities.clear();
    int size = in.readInt();
    float sim;
    for (int i = 0; i < size; i++){
      sim = in.readFloat();
      similarities.add(sim);
    }
    String mv;
    for (int i = 0; i < size; i++){
      mv = Text.readString(in);
      movies.add(mv);
    }
  }
  public void write(DataOutput out) throws IOException {
    out.writeFloat(similarity);
    int size = movies.size();
    out.writeInt(size);
    for(int i = 0; i < size; i++){
      out.writeFloat(similarities.get(i));
    }
    for(int i = 0; i < size; i++){
      Text.writeString(out, movies.get(i));
    }
  }
}
