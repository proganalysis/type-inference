public class Romaybe2 {

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(int d) /*mutable*/ { this.day = d; }
    }

    private /*this-mutable*/ Day f;

    public /*romaybe*/ Day getDay() /*romaybe*/ {
        return f;
    }

    // This method forces f to this-mutable instead of readonly.
    public void foo() /*mutable*/ {
        getDay().setDay(3);
    }
}
