public class Romaybe1 {

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(int d) /*mutable*/ { this.day = d; }
    }

    public /*romaybe*/ Day id(/*romaybe*/ Day d) /*readonly*/ {
        return d;
    }

    // forces id not to have readonly returnt type.
    public void foo(/*mutable*/ Day x) /*readonly*/ {
        id(x).setDay(2);
    }
}
