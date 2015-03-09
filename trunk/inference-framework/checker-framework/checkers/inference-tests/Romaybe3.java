public class Romaybe3 {

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public Day setDay(int d) /*mutable*/ { this.day = d; return this; }
    }

    public /*romaybe*/ Day id(/*romaybe*/ Day d) /*readonly*/ {
        return d;
    }

    // forces id not to have readonly returnt type.
    public void foo(/*mutable*/ Day f) /*readonly*/ {
	Day y = id(f);
	Day x = y.setDay(2);
    }
}
