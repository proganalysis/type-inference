public class Data {
    int d;
    int get() {
	if (d < 0) {
	    d = d + 1;
	}
	return d;
    }
    void set(int p) {
	d = p;
    }
}
