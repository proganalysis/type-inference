public class ThisLeak4 {

	public void n() {
		System.out.println("This is n");
	}
	
	public void o() {
		innerClass ic = new innerClass();
		ic.m();
	}
	
	public static void main(String[] arg) {
		ThisLeak4 tl4 = new ThisLeak4();
		tl4.o();
	}
	
	public class innerClass {
		int f;
		public void m() {
			System.out.println("Boza");
			n();
		}
	}
}
