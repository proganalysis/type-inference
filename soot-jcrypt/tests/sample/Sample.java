package sample;
import checkers.inference.jcrypt.quals.*;
public class Sample {

	public static void main(String[] args) {
		Data ds = new Data();
		int s = getInitial();
		//ds.set(s+1);
		ds.set(s);
		int ss = ds.get();
		ds.set(s+1);
		ss = ds.get();
		Data dc = new Data();
		int c = -3;
		dc.set(c);
		int cc = dc.get();
		
		int ans = ss + cc;
		System.out.print(ans);
	}
	
	private static @Sensitive int getInitial() {
		return -3;
	}

}
