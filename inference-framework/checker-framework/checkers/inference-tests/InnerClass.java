class b {
    public b(int x) {}
}

public class InnerClass {

    public void m() {
	b ref = new b (42) {
	    };
    }
}
