import checkers.inference2.reimN.quals.*;

public class CellClient {

    public static class Cell<T> {
	public /*PolyPoly*/ T val;
	public /*PolyPoly*/ T getVal() /*PolyPoly*/ { return val; }
	public void setVal(/*@MutMut*/ Cell<T> this, T val) /*MutMut*/ { this.val = val;}
    }

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) /*ReadMut*/ { this.day = d; }
        public int getDay() /*ReadRead*/ { return day; }
        public void setDay(/*@MutMut*/ Day this, int d) /*ReadMut*/ { this.day = d; }
    }

    public void foo() {
        Cell<Day> c = new Cell<Day>();
        Day d = c.getVal();
        int day = d.getDay();
    }

    public void bar() {
        /*ReadMut*/ Cell<Day> c = new /*MutMut*/ Cell<Day>();
        Day d = c.getVal();
        int day = d.getDay();
        Day e = new Day();
        c.setVal(e);
    }

    public void baz() {
        /*MutMut*/ Cell<Day> c = new /*MutMut*/ Cell<Day>();
        /*ReadMut*/ Day d = c.getVal();
        d.setDay(3);
    }

    public void quax() {
        /*MutMut*/ Cell<Day> c = new /*MutMut*/ Cell<Day>();
        /*ReadMut*/ Day d = c.getVal();
        d.setDay(5);
        Day e = new Day();
        c.setVal(e);
    }
}
