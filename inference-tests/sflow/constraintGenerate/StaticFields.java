import java.io.*;
public class StaticFields {

    public static String sf = ":";

    public void m1() {
        String s2 = File.separator;
        sf = s2;
        String s1 = sf;
    }
}


class A {
    public void m2() {
        String t = "\\";
        StaticFields.sf = t; 
        String t2 = StaticFields.sf;
    }
}
