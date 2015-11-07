package keshav.com.restservice;

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by Keshav on 10/31/2015.
 */
public class NullHostNameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession sslSession) {
        Log.d( "NullHostNameerifier", "Approving certificate for " + hostname );
        return true;
    }
}