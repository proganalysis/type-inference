public class AssignableField {

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(int d) /*mutable*/ { this.day = d; }
    }

    // That commented @checkers.javari.quals.Assignable is recognized by the extended
    // compiler and supersedes Assignable.fields.
    private /*assignable readonly*/ /*@checkers.javari.quals.Assignable*/ Day d;
    public /*readonly*/ Day foo() /*readonly*/ {
        if (d == null) {
            d = new Day();
        }
        return d;
    }

}
