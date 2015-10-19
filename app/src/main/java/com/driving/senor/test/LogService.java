package com.driving.senor.test;

import android.util.Log;

/**
 * Created by Keshav on 3/1/2015.
 */
public class LogService
{
    public final static String LOG_TAG = "DRIVING SENSOR";
    public final static Boolean DEBUG = true;

    public static void log(String message)
    {
        if (DEBUG)
            Log.v( LOG_TAG, message );
    }
}
