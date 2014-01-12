package edu.rpi;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.io.PrintStream;
import java.io.File;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SourceLocator;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.*;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.tagkit.*; 
import edu.rpi.sflow.*;
import edu.rpi.reim.*;


public class SootInference {
	
	public static void main(String[] args) {
		
		//prefer Android APK files// -src-prec apk
//        Options.v().set_src_prec(Options.src_prec_apk);
		
		//output as APK, too//-f J
//        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_output_format(Options.output_format_jimple);
//        Options.v().set_keep_line_number(true);

        // Exclude packages
        String[] excludes = new String[] {
            "android.annotation",
            "com.android",
            "com.google",
            "org.apache",
            "org.acra",
            "com.loopj",
            "android.support"
        };
        Options.v().set_exclude(Arrays.asList(excludes));

        Scene.v().addBasicClass("java.lang.StringBuilder", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.Math", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.HashMap", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.net.HttpURLConnection", SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.util.LinkedList", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.StatusLine", SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.io.InputStreamReader", SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.lang.IllegalArgumentException", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.NumberFormatException", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.reflect.AccessibleObject", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.reflect.Method", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.String", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.security.MessageDigest", SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.util.Date", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.List", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.zip.GZIPInputStream", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.client.entity.UrlEncodedFormEntity", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.conn.params.ConnPerRoute", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.conn.params.ConnPerRouteBean", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.conn.scheme.PlainSocketFactory", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.conn.scheme.SchemeRegistry", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.conn.scheme.SocketFactory", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.conn.ssl.SSLSocketFactory", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.entity.StringEntity", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.HttpVersion", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.impl.client.DefaultHttpClient", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.message.BasicHeader", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.message.BasicNameValuePair", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.params.BasicHttpParams", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.ProtocolVersion", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.util.CharArrayBuffer", SootClass.HIERARCHY);
        Scene.v().addBasicClass("java.io.File", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.io.FileOutputStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.net.HttpURLConnection", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.net.URL", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.net.URLConnection", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.AbstractList", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.ArrayList", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.LinkedList", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.Queue", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.zip.GZIPInputStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("org.apache.http.HttpResponse", SootClass.SIGNATURES);
        Scene.v().addBasicClass("org.apache.http.StatusLine", SootClass.SIGNATURES);
        Scene.v().addBasicClass("org.apache.http.util.EntityUtils", SootClass.HIERARCHY);
        Scene.v().addBasicClass("org.apache.http.util.EntityUtils", SootClass.SIGNATURES);


//        PackManager.v().getPack("jtp").add(new Transform("jtp.it", new IntentTransformer()));

        InferenceTransformer sflowTransformer = new SFlowTransformer();
        InferenceTransformer reimTransformer = new ReimTransformer();
        PackManager.v().getPack("jtp").add(new Transform("jtp.reim", reimTransformer));
        PackManager.v().getPack("jtp").add(new Transform("jtp.sflow", sflowTransformer));

		soot.Main.main(args);

        String outputDir = SourceLocator.v().getOutputDir();

        System.out.println("INFO: Solving Reim constraints:  " + reimTransformer.getConstraints().size() + " in total...");
        ConstraintSolver cs = new SetbasedSolver(reimTransformer);
        Set<Constraint> errors = cs.solve();
        try {
            PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-constraints.log");
            for (Constraint c : reimTransformer.getConstraints()) {
                reimOut.println(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Constraint c : errors)
            System.out.println(c);
        System.out.println("INFO: Finish solving Reim constraints. " + errors.size() + " error(s)");

        try {
            PrintStream reimOut = new PrintStream(outputDir + File.separator + "reim-result.jaif");
            reimTransformer.printJaif(reimOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Solving SFlow constraints:  " + sflowTransformer.getConstraints().size() + " in total...");
        ConstraintSolver sflowSolver = new SFlowConstraintSolver2(sflowTransformer, reimTransformer.getAnnotatedValues());
        errors = sflowSolver.solve();
        try {
            PrintStream sflowOut = new PrintStream(outputDir + File.separator + "sflow-constraints.log");
            for (Constraint c : sflowTransformer.getConstraints()) {
                sflowOut.println(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        for (Constraint c : errors)
            System.out.println(c + "\n");
        System.out.println("INFO: Finish solving SFlow constraints. " + errors.size() + " error(s)");
        try {
            PrintStream sflowOut = new PrintStream(outputDir + File.separator + "sflow-result.jaif");
            sflowTransformer.printJaif(sflowOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
}
