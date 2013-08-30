import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Test {

  public static void main(String[] args) {
    String fileName = "X.java";
//    String fileName = "list/ListIterator.java";
//    String fileName = "list/LinkedList.java";
//    String fileName = "abc/B.java";
//    String fileName = "../../benchmarks/jolden/bh/BH.java";
//    String fileName = "p/TreeMap_EntrySetView.java";
//    String fileName = "p/HashSet.java";
//    String fileName = "p/ArrayList.java";
    // Read the source file into a buffer.
    StringBuffer source = new StringBuffer();
    FileInputStream in = null;
    try {
      in = new FileInputStream(fileName);
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      int c;
      while ((c = in.read()) != -1)
        bytes.write(c);
      source.append(bytes.toString());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null)
          in.close();
      } catch (IOException e) {
      }
    }

//    Pattern p = Pattern.compile("(public|protected|private|static|\\s)\\s+([\\w\\<\\>\\[\\]]+)\\s+(\\w+)\\s*\\(([^\\)]*)\\)\\s*(\\{?|[^;])");
//    Pattern p = Pattern.compile("([\\w\\<\\>\\[\\]]+)\\s+(\\w+)\\s*\\(([^\\)]*)\\)\\s*");
//    Pattern p = Pattern.compile("(public|protected|private|static|\\s)\\s+([\\w\\<\\>\\[\\]]+)\\s+(\\w+)\\s*\\(([^\\)]*)\\)\\s*\\{?|[^;]");
//    Pattern p = Pattern.compile("(public|protected|private|static|\\s)\\s+([\\w\\<\\>\\[\\]]+)\\s+(\\w+)\\s*\\(([^\\)]*)\\)\\s*(\\{?|[^;])");
//    Pattern p = Pattern.compile("((public|protected|private)\\s+)?(\\w+)\\s*\\(([^\\)]*)\\)\\s*(\\{?|[^;])");
//    Pattern fieldPattern = Pattern.compile("((public|protected|private|static|final)\\s+)?((/\\*[\\w@\\*/ ]+\\*/)\\s+)?([\\w\\<\\>\\[\\]]+)\\s+(\\w+)\\s*(;|=)");
//    Pattern varPattern = Pattern.compile("((/\\*[\\w@\\*/ ]+\\*/)\\s+)?([\\w\\<\\>\\[\\]]+)\\s+(\\w+)\\s*(;|=)");
//    Pattern varPattern = Pattern.compile("((public|protected|private|static|final)\\s+)?((/\\*@\\w+\\*/\\s*)*[\\w\\<\\>]+(\\s*(/\\*@\\w+\\*/\\s*)*\\[\\])*)\\s+(\\w+)\\s*(;|=|,|\\)|:)");
//    Pattern newclassPattern = Pattern.compile("(^|\\s)new\\s+([\\w\\<\\>@\\*/ ]+)\\s*\\(([^\\)]*)\\)");
//    Pattern methodPattern = Pattern.compile("((public|protected|private|static|final)\\s+)?([\\w\\<\\>\\[\\]@\\*/ ]+\\s+)?(\\w+)\\s*\\(([^\\);]*)\\)");
//    Pattern methodPattern = Pattern.compile("((public|protected|private|static|final)\\s+)?(((/\\*@\\w+\\*/\\s*)*[\\w\\<\\>]+(\\s*(/\\*@\\w+\\*/\\s*)*\\[\\])*)\\s+)?(\\w+)\\s*\\(([^\\);]*)\\)");
    Pattern newarrayPattern = Pattern.compile("(^|\\s)new\\s+([\\w\\<\\>@\\*/ ]+)\\s*\\(([^\\)]*)\\)");

    Pattern varPattern = Pattern.compile("((public|protected|private|static|final|transient)\\s+)?((/\\*@\\w+\\*/\\s*)*[\\w\\.\\<\\>]+(\\s*(/\\*@\\w+\\*/\\s*)*\\[\\])*(\\s*/\\*[^/\\*]*\\*/)?)\\s+(\\w+)\\s*(/\\*[^\\*/]*\\*/)?\\s*(;|=|,|\\)|:)");
    Pattern newclassPattern = Pattern.compile("(^|\\s)new\\s+([\\w\\.\\<\\>@\\*/ ]+)(\\s*/\\*[^/\\*]*\\*/)?\\s*\\(([^\\)]*)\\)");
    Pattern methodPattern = Pattern.compile("((public|protected|private|static|final)\\s+)?(((/\\*@\\w+\\*/\\s*)*[\\w\\.\\<\\>]+(\\s*(/\\*@\\w+\\*/\\s*)*\\[\\])*)(\\s*/\\*[^/\\*]*\\*/)?\\s+)?(\\w+)\\s*\\(([^\\);]*)\\)");

//    Pattern p = methodPattern;
//    Pattern p = newclassPattern;
    Pattern p = varPattern;
    Matcher m = p.matcher(source.toString());
    int j = 1;
    while (m.find()) {
      System.out.println("match: " + (j++));
      System.out.println(m.groupCount());
//      System.out.println(m.group(3));
      for (int i = 0; i <= m.groupCount(); i++) {
        System.out.println(i + ":" + m.group(i));
      }
    }
  }
}
