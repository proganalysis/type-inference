
public class InnerClassTest<S> {

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

    Day d;

    // $AIn
    public class AIn<T> {
        public AIn() {

        }
        Cell<T> f;
        public S sf;
    }

    AIn<Day> in;

    public AIn<Day> foo() {
        //InnerClassTest<Day> i = new InnerClassTest<Day>();
        InnerClassTest<S>.AIn<Day> x = this.in;
        return x;
    }

    // $Inner
    public class Inner {
        Cell<Day> c;
    }

    // $Nested static
    public static class Nested {
        Cell<Day> c;
    }


    // $1
    Cell<Day> initializedField = new Cell<Day>() {
        public void initializedFieldFoo() {}
    };



    // $2 static
    static Cell<Day> staticInitializedField = new Cell<Day>() {
        public void staticInitializedFieldFoo() {}
    };

    // $3
    Cell<Day> factory() {
        return new Cell<Day>() {
            public void factoryFoo() {}
            public S var() { return null; }
        };
    }

    // $4 static
    static Cell<Day> staticFactory() {
        return new Cell<Day>() {
            public void factoryFoo() {}
        };
    }
}
