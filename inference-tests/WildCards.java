public class WildCards<T extends /*readonly*/ Object> {

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

    private T f;

    public static /*readonly*/ Day getDay(/*readonly*/ Cell<? extends /*readonly*/ Day> c) {
        return c.getVal();
    }

    public void setF(/*readonly*/ Cell<? extends T> x) /*mutable*/ {
        f = x.getVal();
    }

    public void addF(/*mutable*/ Cell<? super T> y) /*readonly*/ {
        y.setVal(f);
    }

    public static void setDay(/*readonly*/ Cell<? extends /*mutable*/ Day> z) {
        z.getVal().setDay(3);
    }

    public static /*mutable*/ Cell<? super /*readonly*/ Day> baz() {
        return new Cell<Day>();
    }

    public static void quax() {
        /*mutable*/ Day d = new Day();
        d.setDay(3);
        baz().setVal(d);
    }

    public static String printVal(/*readonly*/ Cell<?> c) {
        String s = c.toString();
        return s;
    }
}
