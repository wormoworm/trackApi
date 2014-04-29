package se.sics.trackapi.ski5Cloud;

import static se.sics.trackapi.ski5Cloud.Utils.fmt;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogH {    
	private static int LOGLEVEL = android.util.Log.DEBUG;
    
    private static boolean ASSERT = LOGLEVEL <= android.util.Log.ASSERT;//7
    private static boolean ERROR = LOGLEVEL <= android.util.Log.ERROR;//6
    private static boolean WARN = LOGLEVEL <= android.util.Log.WARN;  //5  
    private static boolean INFO = LOGLEVEL <= android.util.Log.INFO;//4
    
    private static boolean DEBUG = LOGLEVEL <= android.util.Log.DEBUG;//3
    private static boolean VERBOSE = LOGLEVEL <= android.util.Log.VERBOSE;//2
    private static String TAG = "SKI5CLOUD";
    public static void wtf(String string,Object... p) {
        if (ASSERT) android.util.Log.wtf(TAG, fmt(string,p));
    }
    public static void i(String string, Object... p) {
        if (INFO) android.util.Log.i(TAG, fmt(string,p));
    }
    public static void e(String string, Object... p) {
        if (ERROR) android.util.Log.e(TAG, fmt(string,p));
    }
    public static void d(String string, Object... p) {
        if (DEBUG) android.util.Log.d(TAG, fmt(string,p));
    }
    public static void v(String string, Object... p) {
        if (VERBOSE) android.util.Log.v(TAG, fmt(string,p));
    }
    public static void w(String string, Object... p) {
        if (WARN) android.util.Log.w(TAG, fmt(string,p));
    }
    public static String throwableToString(Throwable t) {
        if (t == null)
            return null;

        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}

