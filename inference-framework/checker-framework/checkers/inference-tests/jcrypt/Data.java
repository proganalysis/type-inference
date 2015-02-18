import checkers.inference2.jcrypt.quals.*;

public class Data {
    String secret;
    String get() {
       return this.secret;
    }
    void set(String p) {
       this.secret = p;
    }
}
