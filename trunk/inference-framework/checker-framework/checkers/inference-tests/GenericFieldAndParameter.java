import java.util.*;

public class GenericFieldAndParameter {

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(int d) /*mutable*/ { this.day = d; }
    }

    public Set<Day> set;
    public Set<Integer> ints;

    public GenericFieldAndParameter() {
    }

    // The parameter doesn't get changed; only a field gets changed
    public void foo(Set<Boolean> bools) {
        set.add(new Day());
        int size = set.size();
    }

    public void foo(ArrayList<Red> reds) {
        reds.get(0).set(1);
    }
}
