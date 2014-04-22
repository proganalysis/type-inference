public class ArraySimple1 {

    public void foo()  {
        /*@checkers.inference.sflow.quals.Tainted*/ String imei = "1234";
        /*@checkers.inference.sflow.quals.Safe*/ String obfuscated = "";
        for (char c : imei.toCharArray()) {
            obfuscated += c + "_";
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

}
