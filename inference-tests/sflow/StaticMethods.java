
public class StaticMethods {

    public static String escape(String url) {
        return url;
    }

    public void m1() {
        /*@checkers.inference.sflow.quals.Secret*/ String oldUrl = "";
        String newUrl = escape(oldUrl);
    }

    public void m2() {
        /*@checkers.inference.sflow.quals.Secret*/ String oldUrl = "";
        /*@checkers.inference.sflow.quals.Tainted*/ String newUrl = "";
        newUrl = escape(oldUrl);
    }
}
