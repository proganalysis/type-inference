package kmeans;
import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class Cluster implements Writable{

	public long movie_id;
	public byte[] movie_id_sen;
	public int total;
	public float similarity;
	public ArrayList<Review> reviews;
	
	public Cluster(){
		movie_id = -1;
		movie_id_sen = new byte[16];
		total = 0;
		similarity = 0.0f;
		reviews = new ArrayList<Review>();
	}
	public Cluster(Cluster a){ // copying the cluster
		movie_id = a.movie_id;
		movie_id_sen = a.movie_id_sen;
		total = a.total;
		similarity = a.similarity;
		reviews = new ArrayList<Review>();
		Review rv;
		for(int i = 0; i < a.reviews.size(); i++){
			rv = new Review(a.reviews.get(i)); 
			reviews.add(rv);
		}
	}
	public void readFields(DataInput in) throws IOException {
		movie_id = in.readLong();
		in.readFully(movie_id_sen);
		total = in.readInt();
		similarity = in.readFloat();
		reviews.clear();
		int size = in.readInt();
		Review rs; 
		for (int i = 0; i < size; i++){
			rs = new Review();
			rs.rater_id = in.readInt();
			in.readFully(rs.rater_id_sen);
			rs.rating = in.readByte();
			reviews.add(rs);
		}
	}
	public void write(DataOutput out) throws IOException {
		out.writeLong(movie_id);
		out.write(movie_id_sen);
		out.writeInt(total);
		out.writeFloat(similarity);
		int size = reviews.size();
		out.writeInt(size);
		for(int i = 0; i < size; i++){
			out.writeInt(reviews.get(i).rater_id);
			out.write(reviews.get(i).rater_id_sen);
			out.writeByte(reviews.get(i).rating);
		}
	}
}
