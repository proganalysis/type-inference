public class MutableField {

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

    // That commented @checkers.javari.quals.Mutable is recognized by the extended
    // compiler and supersedes Mutable.fields.
    /*mutable*/ /*@checkers.javari.quals.Mutable*/ Day d;

    // TODO: update this test case once compiler has generic annotations
    // in class file.
    /*this-mutable*/ Cell</*mutable*/ /*!@checkers.javari.quals.Mutable*/ Day> f;

    public void foo() /*readonly*/ {
        d.setDay(3);
    }

    public void bar() /*mutable*/ {
        d = new Day();
    }

    public void baz() /*readonly*/ {
        /*mutable*/ Day x = f.getVal();
        x.setDay(2);
    }

    public void quax() /*mutable*/ {
        f.setVal(new Day(3));
    }
}
