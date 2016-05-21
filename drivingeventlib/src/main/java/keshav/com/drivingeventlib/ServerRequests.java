package keshav.com.drivingeventlib;

import android.content.Context;
import android.util.Log;

import keshav.com.restservice.HTTPRestTask;
import keshav.com.restservice.HTTPRestTaskEvent;
import keshav.com.restservice.OnTaskComplete;
import keshav.com.restservice.RequestType;
import keshav.com.utilitylib.LogService;

/**
 * Static methods for accessing REST API
 * Created by Keshav on 2/25/2016.
 */
public class ServerRequests  {

    public static final String APPLICATION_SERVER = "http://104.236.253.74/";
    public static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String API_KEY = "YWUZzv7W9E2lBHJxl7jJXfVo670d6K5g";
    public static final String API_KEY_WEATHER = "65977a9629f90451619597376b668699";

    /**
     * Sends acceleration data to api
     * @param userid
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static boolean postAccelerationData(Context context, String userid, float x, float y, float z) {

        if (GLOBALS.DEBUG_MODE)
            return true;
        try {
            HTTPRestTask task = new HTTPRestTask( context, RequestType.POST );
            task.setURL( APPLICATION_SERVER + "addaccsensordata" );
            task.addHeaderParam( "X-API-KEY", API_KEY );
            task.addBodyParam( "userid", userid );
            task.addBodyParam( "accx", x );
            task.addBodyParam( "accy", y );
            task.addBodyParam( "accz", z );
            task.execute();
            return true;
        } catch (Exception ex) {
            LogService.log( "Exception occurred rest server: " + ex.getMessage() );
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
    public static boolean postGPSData(Context context, String userid, String latitude, String longitude,
                                      float speed, int weatherid, int rain, double temp, double wind,
                                      double pressure, double humidity ) {

        if (GLOBALS.DEBUG_MODE)
            return true;
        try {
            HTTPRestTask task = new HTTPRestTask( context, RequestType.POST );
            task.setURL( APPLICATION_SERVER + "addgpsdata" );
            task.addHeaderParam( "X-API-KEY", API_KEY );
            task.addBodyParam( "userid", userid );
            task.addBodyParam( "latitude", latitude );
            task.addBodyParam( "longitude", longitude );
            task.addBodyParam( "speed", speed );
            task.addBodyParam( "weatherid", weatherid );
            task.addBodyParam( "rain", rain );
            task.addBodyParam( "temp", temp );
            task.addBodyParam( "wind", wind );
            task.addBodyParam( "pressure", pressure );
            task.addBodyParam( "humidity", humidity );
            task.execute();
            return true;
        } catch (Exception ex) {
            LogService.log( "Exception occurred rest server: " + ex.getMessage() );
            return false;
        }
    }

    /**
     * Gets the current weather data using open weather api.
     * @param context
     * @param latitude current latitude of device
     * @param longitude current longitude of device
     * @return weather data in Json String format
     */
    public static String getCurrentWeatherDataEvent( Context context, String latitude, String longitude ) {

        try {
            HTTPRestTask task = new HTTPRestTaskEvent( context, RequestType.POST );
            task.setURL( WEATHER_API_URL + "weather" );
            task.addHeaderParam( "X-API-KEY", API_KEY_WEATHER );
            task.addBodyParam( "lat", latitude );
            task.addBodyParam( "lon", longitude );
            task.execute();
            return task.getResponse();
        } catch ( Exception ex ) {
            LogService.log("Error occurred retrieving weather data. " + ex.getMessage());
            return "";
        }
    }

    public static String getCurrentWeatherData ( Context context, OnTaskComplete listener, String latitude, String longitude ) {

        try {
            HTTPRestTask task = new HTTPRestTask ( context, RequestType.POST, listener );
            task.setURL( WEATHER_API_URL + "weather" );
            task.addHeaderParam( "X-API-KEY", API_KEY_WEATHER );
            task.addBodyParam( "lat", latitude );
            task.addBodyParam( "lon", longitude );
            task.execute();
            return task.getResponse();
        } catch ( Exception ex ) {
            LogService.log("Error occurred retrieving weather data. " + ex.getMessage());
            return "";
        }
    }

}
