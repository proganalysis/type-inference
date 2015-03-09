import checkers.inference2.reimN.quals.*;

public class IdCall {

    public void foo(Bar1 a, Bar1 c) {
	X x = new X();
	Bar1 b = x.id(a);
	b.g = 0;
	Bar1 d = x.id(c);
    }

}
