class ZZZ {
	ThisLeak3 f;
}
public class ThisLeak3 {
	void m(ZZZ z, ThisLeak3 p) {
		z.f = p;
	}
	
	void n(ZZZ z) {
		m(z,this);
	}
	
	public static void main(String[] arg) {
		ThisLeak3 tl3 = new ThisLeak3();
		ZZZ z = new ZZZ();
		tl3.n(z);
	}
}
