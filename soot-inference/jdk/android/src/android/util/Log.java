package android.util;
import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 
public final class Log {
    public abstract static interface TerribleFailureHandler {
        public abstract void onTerribleFailure(java.lang.String arg0, android.util.Log.TerribleFailure arg1);
    }
    private static class TerribleFailure extends java.lang.Exception {
        TerribleFailure(java.lang.String arg0, java.lang.Throwable arg1) { throw new RuntimeException("skeleton method"); }
    }
    public static final int VERBOSE = 1;
    public static final int DEBUG = 1;
    public static final int INFO = 1;
    public static final int WARN = 1;
    public static final int ERROR = 1;
    public static final int ASSERT = 1;
    private static android.util.Log.TerribleFailureHandler sWtfHandler;
    public static final int LOG_ID_MAIN = 1;
    public static final int LOG_ID_RADIO = 1;
    public static final int LOG_ID_EVENTS = 1;
    public static final int LOG_ID_SYSTEM = 1;
    public static int d(java.lang.String arg0, @Safe java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public static int d(java.lang.String arg0, @Safe java.lang.String arg1, java.lang.Throwable arg2) { throw new RuntimeException("skeleton method"); }
    public static int e(java.lang.String arg0, @Safe java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public static int e(java.lang.String arg0, @Safe java.lang.String arg1, java.lang.Throwable arg2) { throw new RuntimeException("skeleton method"); }
    public static java.lang.String getStackTraceString(java.lang.Throwable arg0) { throw new RuntimeException("skeleton method"); }
    public static int i(java.lang.String arg0, @Safe java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public static int i(java.lang.String arg0, @Safe java.lang.String arg1, java.lang.Throwable arg2) { throw new RuntimeException("skeleton method"); }
    public static boolean isLoggable(java.lang.String arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public static int println(int arg0, java.lang.String arg1, @Safe java.lang.String arg2) { throw new RuntimeException("skeleton method"); }
    public static int println_native(int arg0, int arg1, java.lang.String arg2, @Safe java.lang.String arg3) { throw new RuntimeException("skeleton method"); }
    public static android.util.Log.TerribleFailureHandler setWtfHandler(android.util.Log.TerribleFailureHandler arg0) { throw new RuntimeException("skeleton method"); }
    public static int v(java.lang.String arg0, @Safe java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public static int v(java.lang.String arg0, @Safe java.lang.String arg1, java.lang.Throwable arg2) { throw new RuntimeException("skeleton method"); }
    public static int w(java.lang.String arg0, @Safe java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public static int w(java.lang.String arg0, @Safe java.lang.String arg1, java.lang.Throwable arg2) { throw new RuntimeException("skeleton method"); }
    public static int w(@Safe java.lang.String arg0, java.lang.Throwable arg1) { throw new RuntimeException("skeleton method"); }
    public static int wtf(java.lang.String arg0, @Safe java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public static int wtf(java.lang.String arg0, @Safe java.lang.String arg1, java.lang.Throwable arg2) { throw new RuntimeException("skeleton method"); }
    public static int wtf(java.lang.String arg0, java.lang.Throwable arg1) { throw new RuntimeException("skeleton method"); }
}
