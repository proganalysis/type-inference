/*>>> 
import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 
*/
class ImplicitTest {

    void main() {
        /*@Tainted*/ String source = "source";
        /*@Safe*/ String sink = "sink";
        Util util = new Util();
        String lowerSource = util.toLowerCase(source);
        /*@Safe*/ String lowerSink = util.toLowerCase(source);
        /*@Safe*/ String lowerSink2 = util.toLowerCase(sink);

        util.set(source);
        sink = util.get();
    }
    
}

class Util {

    private String f = new String("a");

    String toLowerCase(String s) {
        String s1 = s;
        return s1;  // param -> ret
    }

    String get() {
        String s = this.f;
        return s; // this -> ret
    }

    void set(String newf) {
        String n = newf;
        this.f  = n;
    }

    String newInstance(String id) {
        return "id"; // return something new
    }
}
