public class SuperSubType {

  public static class Day {
    public int day;
    public Day() { }
    public Day(int d) { this.day = d; }
    public int getDay() /*readonly*/ { return day; }
    public void setDay(int d) /*mutable*/ { this.day = d; }
  }

  public static class SuperType {

    public void foo(Day sx) {
      sx.setDay(3);
    }

  }

  public static class Subtype extends SuperType {

    private Day d;

    public void foo(Day x) {
      d.setDay(3);
    }

  }

}
