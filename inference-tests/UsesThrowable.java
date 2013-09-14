public class UsesThrowable {

  public static void throwException() {
    throw new RuntimeException("detail message");
  }

  public static void catchException() {
      try {
          throw new RuntimeException("test");
      }
      catch (Throwable t) {
          System.err.println(t.toString());
      }
  }

}
