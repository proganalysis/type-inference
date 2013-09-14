public class OpenWorld<T extends /*mutable*/ OpenWorld.Day> {

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

    public /*this-mutable*/ Cell</*this-mutable*/ Day> f;

    private /*this-mutable*/ Day d;

    public T t;

    public /*romaybe*/ Day getDay() /*romaybe*/ {
        return d;
    }

}
