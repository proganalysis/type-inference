
/**
 * Tests things such as Object o = Day[] and Object[] a = Day[][].
 */
public class SpecialObjectCases {

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

    public static void foo() {
        Object o = new Day[3];
        o = new Cell<Day>();
        Object[] a = new Day[3][1];
    }

    public static String bar(Object o) {
        if (o.hashCode() < 0) {
            o = new Cell<Day>();
        } else if (o.hashCode() > 10) {
            o = new Day[3];
        }
        String s = o.toString();
        return s;
    }
}
