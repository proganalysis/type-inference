package kmeansModified;
import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Review implements Writable {

  public byte[] rater_id_sen;
  public int rater_id;
  public byte rating;

  public Review() {
    rater_id_sen = new byte[8];
    rater_id = -1;
    rating = 0;
  }

  public Review(Review a){
    rater_id = a.rater_id;
    rater_id_sen = a.rater_id_sen;
    rating = a.rating;
  }
  public void clear(){
    rater_id = -1;
    rater_id_sen = new byte[8];
    rating = 0;
  }
  public void readFields(DataInput in) throws IOException {
    rater_id = in.readInt();
    in.readFully(rater_id_sen);
    rating = in.readByte();
  }

  public void write (DataOutput out) throws IOException {
    out.writeInt(rater_id);
    out.write(rater_id_sen);
    out.writeByte(rating);
  }
}
