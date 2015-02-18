import checkers.inference2.reimN.quals.*;

public class Arrays {

    public static class Day {
        public int day;
        public Day() { }
        public Day(int d) { this.day = d; }
        public int getDay() /*readonly*/ { return day; }
        public void setDay(/*@MutMut*/ Day this, int d) /*mutable*/ { this.day = d; }
    }

    public void baz() /*readonly*/ {
        /*mutable*/ Day[/*mutable*/] ma = new Day[1];
        ma[0] = new Day();
        ma[0].setDay(2);

        /*? readonly*/ Day[/*readonly*/] ra = ma;
        /*readonly*/ Day x = ra[0];
        int y = x.getDay();

     }

}
