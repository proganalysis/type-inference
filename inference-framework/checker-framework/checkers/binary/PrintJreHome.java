/**
 * Prints the value of property "java.home", which is the location
 * where the JRE is installed.
 */
public class PrintJreHome {
  public static void main(String[] args) {
    System.out.println(System.getProperty("java.home"));
  }
}
