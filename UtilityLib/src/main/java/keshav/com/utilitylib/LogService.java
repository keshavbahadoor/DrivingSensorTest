package keshav.com.utilitylib;

import android.util.Log;
import android.widget.TextView;

/**
 * Created by Keshav on 3/1/2015.
 */
public class LogService
{
    public final static String LOG_TAG = "DRIVING SENSOR";
    public final static Boolean DEBUG = true;
    private static TextView logWindow;

    public static void log(String message)
    {
        if (DEBUG) {
            Log.v( LOG_TAG, message );
        }
        if (logWindow != null){
            logWindow.append( message );
        }
    }

    public static void setLogWindow(TextView logWindow)
    {
        logWindow = logWindow;
    }
}
