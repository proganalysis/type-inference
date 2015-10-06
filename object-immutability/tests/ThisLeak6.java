class YYY {
	ThisLeak6 f;
}

public class ThisLeak6 {
	int x;
	
	public ThisLeak6 id() {
		return this;
	}
	
	public void m() {
		System.out.println("fjdfkjd");
		ThisLeak6 tl6 = id();
		tl6.x = 0;
	}
	
	public void n(YYY y) {
		y.f = id();
	}
	
}
