class Boza {
	ThisLeak5 f;
	int y;
}
public class ThisLeak5 {
	int x;
	Boza b;
	
	public ThisLeak5() {
		ThisLeak5 p = this;
		
		p.b = new Boza();
		p.x = 0;
		
		int y = p.x;
		Boza bb = p.b;
		this.b = bb;
		this.x = y;
	}
	
}
