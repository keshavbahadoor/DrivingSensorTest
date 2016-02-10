package keshav.com.utilitylib;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by Laptop on 5/2/2015.
 */
public class ServiceUtil
{
    private static final String LOG_TAG = ServiceUtil.class.getName();

    /**
     * returns true if a service is running, false if otherwise
     * @param serviceClass
     * @param context
     * @return
     */
    public static boolean isServiceRunning(Class<?> serviceClass, Context context)
    {
        ActivityManager manager = (ActivityManager)context. getSystemService( Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                Log.i( "Service already", "running" );
                return true;
            }
        }
        Log.i( LOG_TAG, "running" );
        return false;
    }

}
