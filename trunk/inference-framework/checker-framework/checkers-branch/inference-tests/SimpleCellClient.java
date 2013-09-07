public class SimpleCellClient {

    public static class SimpleCell<T extends /*readonly*/ Object> {
        T val;
    }

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(int d) /*mutable*/ { this.day = d; }
    }

    public void foo() {
        /*readonly*/ SimpleCell</*readonly*/ Day> c = new SimpleCell<Day>();
        /*readonly*/ Day d = c.val;
        int day = d.getDay();
    }

    public void bar() {
        /*mutable*/ SimpleCell</*readonly*/ Day> c = new SimpleCell<Day>();
        /*readonly*/ Day d = c.val;
        int day = d.getDay();
        /*readonly*/ Day e = new Day();
        c.val = e;
    }

    public void baz() {
        /*readonly*/ SimpleCell</*mutable*/ Day> c = new SimpleCell<Day>();
        /*mutable*/ Day d = c.val;
        d.setDay(3);
    }

    public void quax() {
        /*mutable*/ SimpleCell</*mutable*/ Day> c = new SimpleCell<Day>();
        /*mutable*/ Day d = c.val;
        d.setDay(5);
        Day e = new Day();
        c.val = e;
    }
}
