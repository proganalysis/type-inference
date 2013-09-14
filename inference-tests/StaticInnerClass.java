/**
 * This class tests static inner classes.
 */
public class StaticInnerClass {

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(int d) /*mutable*/ { this.day = d; }
    }

    /*this-mutable*/ Foo f;

    public void bar() /*mutable*/ {
        /*readonly*/ Day d = new Day();
        /*mutable*/ Foo x = new Foo(d);
        f = x;
        f.d = new Day();

        /*mutable*/ Foo y = new Foo(new Day()); //assigned to this-mut field
        f = y;
        int i = f.d.getDay();

        /*readonly*/ Foo z = new Foo(new Day());

        int j = f.d.getDay();

    }

    public static class Foo {
        /*readonly*/ Day d;
        public Foo(/*readonly*/ Day d) {
            this.d = d;
        }
    }


}
