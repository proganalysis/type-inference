public class StaticMethods {

    public static class Cell<T> {
	public T val;
	public T getVal() { return val; }
	public void setVal(T val) { this.val = val; }
    }

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(int d) /*mutable*/ { this.day = d; }
    }

    public static /*romaybe*/ Day id(/*romaybe*/ Day d) {
        return d;
    }

    public static void foo(/*mutable*/ Day x) {
        id(x).setDay(3);
    }

    public static /*mutable*/ Cell</*mutable*/ Day> baz() {
        return new Cell<Day>();
    }

    public static void quax() {
        baz().setVal(new Day());
        baz().getVal().setDay(3);
    }


}
