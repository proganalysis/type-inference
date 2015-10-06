class Helper {
	ThisLeak2 f;
}

public class ThisLeak2 {
	public void m(Helper h) {
		n(h);
	}
	public void n(Helper h) {
		ThisLeak2[] arr = new ThisLeak2[1];
		arr[0] = this;
		// h.f = this;
	}
	
	public static void main(String[] arg) {
		ThisLeak2 tl2 = new ThisLeak2();
		Helper h = new Helper();
		tl2.m(h);
	}
}
