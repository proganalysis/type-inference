public class CellClient {

    public static class Cell<T> {
	public T val;
	public T getVal() { return val; }
	public void setVal(T val) { this.val = val;}
    }

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(int d) /*mutable*/ { this.day = d; }
    }

    public void foo() {
        /*readonly*/ Cell</*readonly*/ Day> c = new Cell<Day>();
        /*readonly*/ Day d = c.getVal();
        int day = d.getDay();
    }

    public void bar() {
        /*mutable*/ Cell</*readonly*/ Day> c = new Cell<Day>();
        /*readonly*/ Day d = c.getVal();
        int day = d.getDay();
        /*readonly*/ Day e = new Day();
        c.setVal(e);
    }

    public void baz() {
        /*readonly*/ Cell</*mutable*/ Day> c = new Cell<Day>();
        /*mutable*/ Day d = c.getVal();
        d.setDay(3);
    }

    public void quax() {
        /*mutable*/ Cell</*mutable*/ Day> c = new Cell<Day>();
        /*mutable*/ Day d = c.getVal();
        d.setDay(5);
        /*mutable*/ Day e = new Day();
        c.setVal(e);
    }
}
