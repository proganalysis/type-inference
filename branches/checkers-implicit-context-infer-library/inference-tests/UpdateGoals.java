import java.io.*;

public class UpdateGoals {

    public static void main(String[] args) {
            File dir = new File(".");
            for (File f : dir.listFiles()) {
                if (f.getName().endsWith(".goal")) {
                    String name = f.getName().substring(0, f.getName().length() - 5);
                    File output = new File(dir, name+".jaif");
                    if (output.exists()) {
                        f.delete();
                        output.renameTo(f);
                    }
                }
            }
    }

}
