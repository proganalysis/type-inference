/**
 * 
 */
package checkers.inference2.typeCast;

import static com.esotericsoftware.minlog.Log.info;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import checkers.inference2.InferenceChecker;
import checkers.inference2.InferenceMain;
import checkers.util.CheckerMain;

import com.sun.tools.javac.main.Main;

/**
 * Those helper methods are from {@link CheckerMain}
 * @author dongy6
 *
 */
public class TypeCastMain extends InferenceMain {

    private static final String VERSION = "1";
    
    public static final String outputDir = "infer-output";
    
    public static String outputDirTrans = "/home/yao/Projects/temp/src/";
    
    public static String packageName;
    
    private static TypeCastMain inferenceMain = null;

    public InferenceChecker checker; 
    
    public TypeCastMain() {
    	checker = null;
    }
    
    public static TypeCastMain getInstance() {
        synchronized (TypeCastMain.class) {
            if (inferenceMain == null) {
                String mainClass = System.getProperty("mainClass");
                if (mainClass != null) {
                    try {
                        inferenceMain = (TypeCastMain) Class.forName(mainClass).newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                else {
                    inferenceMain = new TypeCastMain();
                }
                inferenceMain.needCheck = (System.getProperty("noCheck") == null);
            }
        }
    	return inferenceMain;
    }
    
    public static void destroy() {
    	inferenceMain = null;
    }

	public InferenceChecker getInferenceChcker() {
		return checker;
	}

	public void setInferenceChcker(InferenceChecker inferenceChcker) {
		this.checker = inferenceChcker;
	}
	
	public boolean infer(String[] args, String jdkBootPaths, PrintWriter out) {
		for (String path : jdkBootPaths.split(File.pathSeparator)) {
			if (!path.equals("") && !new File(path).exists()) 
				throw new RuntimeException("Cannot find the boot jar: " + path);
		}
		
		System.setProperty("sun.boot.class.path",
				jdkBootPaths + ":" + System.getProperty("sun.boot.class.path"));
		
		List<String> argList = new ArrayList<String>(args.length + 10);
        String reimCheckerPath = "checkers.inference2.reim.ReimChecker";
        //String jcryptCheckerPath = "checkers.inference2.jcrypt.JcryptChecker";
        //String jcrypt2CheckerPath = "checkers.inference2.jcrypt2.Jcrypt2Checker";
        String transformCheckerPath = "";
        int processorIndex = -1;
        for (int i = 0; i < args.length; i++) {
        	String arg = args[i];
        	// Intercept the processor and replace it with ReimChecker
        	if (arg.equals("-processor")) {
        		argList.add(arg);
        		argList.add(reimCheckerPath);	
				processorIndex = ++i;
				transformCheckerPath = args[processorIndex];
        	}
        	else
	        	argList.add(arg);
        }

		// Add arguments
		argList.add("-Xbootclasspath/p:" + jdkBootPaths);
		argList.add("-proc:only");
		argList.add("-Ainfer");
		argList.add("-Awarns");
		// transform
		info("Transforming...");
		argList.set(processorIndex, transformCheckerPath);
		return transform(argList, out);
	}
	
	private boolean transform(List<String> args, PrintWriter out) {
		com.sun.tools.javac.main.Main main = new com.sun.tools.javac.main.Main("javac", out);
        if (main.compile(args.toArray(new String[0])) != Main.Result.OK)
        	return false;
        ((TypeCastChecker) checker).printResult();
        return true;
	}
	
	public boolean check(String[] args, String jdkBootPaths, PrintWriter out) {
		System.setProperty("sun.boot.class.path",
				jdkBootPaths + ":" + System.getProperty("sun.boot.class.path"));
		List<String> argList = new ArrayList<String>(args.length + 10);
		argList = new ArrayList<String>(args.length + 10);
		for (String arg : args)
			argList.add(arg);

		argList.add("-Xbootclasspath/p:" + jdkBootPaths);
		argList.add("-Awarns");
		argList.add("-proc:only");
		com.sun.tools.javac.main.Main main = new com.sun.tools.javac.main.Main(
				"javac", out);
		if (main.compile(argList.toArray(new String[0])) != Main.Result.OK) {
			return false;
		}
		return true;
	}
	
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String jdkBootPaths = findPathJar(TypeCastMain.class);
		if (jdkBootPaths == "" || !jdkBootPaths.contains("jdk.jar")) {
			String customJDK = System.getProperty("jdk");
			if (customJDK != null)
				jdkBootPaths += File.pathSeparator + customJDK;
			else
				jdkBootPaths += File.pathSeparator + jdkJar();
		}
		String bootStr = "-Xbootclasspath/p:";
		for (String arg : args) {
			if (arg.startsWith(bootStr)) {
				jdkBootPaths += File.pathSeparator
						+ arg.substring(bootStr.length());
				break;
			}
		}
		info("main", "Boot path: " + jdkBootPaths);
		if (outputDir != null && outputDir.compareTo(".") != 0) {
			File dir = new File(outputDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
		}

		TypeCastMain inferenceMain = TypeCastMain.getInstance();
		long startTime = System.currentTimeMillis();
		if (!inferenceMain.infer(args, jdkBootPaths, new PrintWriter(
				System.err, true))) {
			return;
		}

		info("main", "Inference finished");
		info("main", "inference_time:\t"
				+ String.format("%6.1f seconds",
						(float) (System.currentTimeMillis() - startTime) / 1000));

		if (inferenceMain.needCheck) {
			startTime = System.currentTimeMillis();
			inferenceMain.check(args, jdkBootPaths, new PrintWriter(System.err,
					true));
			info("main", "Checking finished");
			info("main", "checking_time:\t"
					+ String.format(
							"%6.1f seconds",
							(float) (System.currentTimeMillis() - startTime) / 1000));
		} else {
			info("main", "Skip checking");
		}
	}
	
	
    protected static File tempJDKPath() {
        String userSupplied = System.getProperty("jsr308.jdk");
        if (userSupplied != null)
            return new File(userSupplied);

        String tmpFolder = System.getProperty("java.io.tmpdir");
        File jdkFile = new File(tmpFolder, "jdk-" + VERSION + ".jar");
        return jdkFile;
    }
	
	/** returns the path to annotated JDK */
    protected static String jdkJar() {
        // case 1: running from binary
        String thisJar = findPathJar(TypeCastMain.class);
        File potential = new File(new File(thisJar).getParentFile(), "jdk.jar");
        if (potential.exists()) {
            return potential.getPath();
        }

        // case 2: there was a temporary copy
        File jdkFile = tempJDKPath();
        if (jdkFile.exists()) {
            return jdkFile.getPath();
        }

        // case 3: extract zipped jdk.jar
        try {
            extractFile(thisJar, "jdk.jar", jdkFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (jdkFile.exists()) {
            return jdkFile.getPath();
        }

        throw new AssertionError("Couldn't find annotated JDK");
    }

    protected static void extractFile(String jar, String fileName, File output) throws Exception {
        int BUFFER = 2048;

        File jarFile = new File(jar);
        ZipFile zip = new ZipFile(jarFile);

        ZipEntry entry = zip.getEntry(fileName);
        assert !entry.isDirectory();

        BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
        int currentByte;
        // establish buffer for writing file
        byte data[] = new byte[BUFFER];

        // write the current file to disk
        FileOutputStream fos = new FileOutputStream(output);
        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

        // read and write until last byte is encountered
        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, currentByte);
        }
        dest.flush();
        dest.close();
        is.close();
        zip.close();
    }

    /**
     * Find the jar file containing the annotated JDK (i.e. jar containing
     * this file
     */
    protected static String findPathJar(Class<?> context) throws IllegalStateException {
        if (context == null) context = CheckerMain.class;
        String rawName = context.getName();
        String classFileName;
        /* rawName is something like package.name.ContainingClass$ClassName. We need to turn this into ContainingClass$ClassName.class. */ {
            int idx = rawName.lastIndexOf('.');
            classFileName = (idx == -1 ? rawName : rawName.substring(idx+1)) + ".class";
        }

        String uri = context.getResource(classFileName).toString();
        if (uri.startsWith("file:")) throw new IllegalStateException("This class has been loaded from a directory and not from a jar file.");
        if (!uri.startsWith("jar:file:")) {
            int idx = uri.indexOf(':');
            String protocol = idx == -1 ? "(unknown)" : uri.substring(0, idx);
            throw new IllegalStateException("This class has been loaded remotely via the " + protocol +
                    " protocol. Only loading from a jar on the local file system is supported.");
        }

        int idx = uri.indexOf('!');
        //As far as I know, the if statement below can't ever trigger, so it's more of a sanity check thing.
        if (idx == -1) throw new IllegalStateException("You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");

        try {
            String fileName = URLDecoder.decode(uri.substring("jar:file:".length(), idx), Charset.defaultCharset().name());
            return new File(fileName).getAbsolutePath();
        } catch (UnsupportedEncodingException e) {
            throw new InternalError("default charset doesn't exist. Your VM is borked.");
        }
    }
}
