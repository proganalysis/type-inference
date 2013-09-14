/**
 * Little program that checks to see if every file ending in .diff in
 * the current directory is empty.  For each file, displays whether or not
 * it is empty.  If any file is non-empty, exits with an error exit status,
 * else exits with a 0 exit status.
 *
 * Usage: java VerifyDiffs [--show_all] [--show_none]
 *
 * If --show_all option is used, all tests that pass will also be displayed.
 * If --show_all is not used, and all tests pass, then there will be no
 * output.
 * If --show_none is used, then there is never any output; use this when you
 * only care about the exit status.
 */
import java.io.*;
import java.util.*;

public class VerifyDiffs {
    enum OutputLevel { NONE, NORMAL, ALL };

    private static OutputLevel outputLevel = OutputLevel.NORMAL;

    private static void parseArgs(String[] args) {
        for(String s : args) {
            if(s.equals("--show_all")) {
              outputLevel = OutputLevel.ALL;
            }
            if(s.equals("--show_none")) {
              outputLevel = OutputLevel.NONE;
            }
        }
    }

    public static void main(String[] args) {
        parseArgs(args);

        boolean pass = true;
        try {

            File dir = new File(".");
            List<File> allDiffs = new ArrayList<File>();
            gatherDiffs(allDiffs, dir);
            Collections.sort(allDiffs);
            for(File f : allDiffs) {
              String fileName = f.toString();
              if (fileName.startsWith("./")) {
                fileName = fileName.substring(2);
              }
              if(f.length() != 0) { // if not empty, output error message
                if(outputLevel != OutputLevel.NONE) {
                  System.out.println(fileName + " ...FAILED");
                }
                pass = false;
              } else {
                if(outputLevel == OutputLevel.ALL) {
                  System.out.println(fileName + " ...OK");
                }
              }
            }
         } catch(Exception e) {
            System.out.println("verify diffs failed due to exception: "
                               + e.getMessage());
            pass = false;
        }

        if(pass) {
            if(outputLevel == OutputLevel.ALL) {
                System.out.println("All tests succeeded.");
            }
        } else {
            System.out.println("Tests failed.");
            System.exit(1);
        }
    }

    /**
     * Recursively adds all files in directory dir ending in .diff to
     * the list diffs.
     *
     * @param diffs the array to place all diff files in
     * @param dir the directory to start gathering diffs
     */
    private static void gatherDiffs(List<File> diffs, File dir) {
      for(File f : dir.listFiles()) {
//      File[] fs = dir.listFiles();
//      for(File f : fs) {
        if(f.toString().endsWith(".diff")) {
          diffs.add(f);
        }
        if(f.isDirectory()) {
          gatherDiffs(diffs, f);
        }
      }
    }
}
