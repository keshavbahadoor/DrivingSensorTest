package keshav.com.drivingeventlib;

import android.content.Context;

import keshav.com.restservice.HTTPRestTask;
import keshav.com.restservice.RequestType;

/**
 * Static methods for accessing REST API
 * Created by Keshav on 2/25/2016.
 */
public class ServerRequests  {

    public static final String SERVER = "http://104.236.253.74/";
    public static final String API_KEY = "YWUZzv7W9E2lBHJxl7jJXfVo670d6K5g";

    /**
     * Sends acceleration data to api
     * @param userid
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static boolean postAccelerationData(Context context, String userid, float x, float y, float z) {

        try {
            HTTPRestTask task = new HTTPRestTask( context, RequestType.POST );
            task.setURL( SERVER + "addaccsensordata" );
            task.addHeaderParam( "X-API-KEY", API_KEY );
            task.addBodyParam( "userid", userid );
            task.addBodyParam( "accx", x );
            task.addBodyParam( "accy", y );
            task.addBodyParam( "accz", z );
            task.execute();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Adds GPS Data to server via api
     * @param context
     * @param userid
     * @param latitude
     * @param longitude
     * @param speed
     * @return
     */
    public static boolean postGPSData(Context context, String userid, String latitude, String longitude, float speed ) {

        try {
            HTTPRestTask task = new HTTPRestTask( context, RequestType.POST );
            task.setURL( SERVER + "addgpsdata" );
            task.addHeaderParam( "X-API-KEY", API_KEY );
            task.addBodyParam( "userid", userid );
            task.addBodyParam( "latitude", latitude );
            task.addBodyParam( "longitude", longitude );
            task.addBodyParam( "speed", speed );
            task.execute();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
