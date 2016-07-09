package keshav.com.utilitylib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Keshav on 2/10/2016.
 */
public class NetworkUtil {

    /**
     * Checks for internet connection
     * requires <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * @param context
     * @return true if internet access available, false if otherwise
     *
     * ref: http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Checks for internet connection
     * requires <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * @param context
     * @return true if internet access available, false if otherwise
     *
     * ref: http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     */
    public static boolean isNetworkAvailableOrConnecting(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    /**
     * Returns true if WIFI is available. False if otherwise
     * @param context Application context (Current)
     * @return boolean
     */
    public static boolean isWIFIAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected() && (netInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
