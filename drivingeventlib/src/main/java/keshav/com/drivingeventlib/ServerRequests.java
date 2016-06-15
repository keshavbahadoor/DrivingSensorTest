package keshav.com.drivingeventlib;

import android.content.Context;
import android.util.Log;

import api.BulkData;
import api.Response;
import api.ServerAPI;
import keshav.com.restservice.HTTPRestTask;
import keshav.com.restservice.HTTPRestTaskEvent;
import keshav.com.restservice.OnTaskComplete;
import keshav.com.restservice.RequestType;
import keshav.com.utilitylib.LogService;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Static methods for accessing REST API
 * Created by Keshav on 2/25/2016.
 */
public class ServerRequests  {
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
//            HTTPRestTask task = new HTTPRestTask( context, RequestType.POST );
//            task.setURL( GLOBALS.APPLICATION_SERVER + "addaccsensordata" );
//            task.addHeaderParam( "X-API-KEY", GLOBALS.API_KEY );
//            task.addBodyParam( "userid", userid );
//            task.addBodyParam( "accx", x );
//            task.addBodyParam( "accy", y );
//            task.addBodyParam( "accz", z );
//            task.execute();
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
            task.setURL( GLOBALS.WEATHER_API_URL + "weather" );
            task.addHeaderParam( "X-API-KEY", GLOBALS.API_KEY_WEATHER );
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
            task.setURL( GLOBALS.WEATHER_API_URL + "weather" );
            task.addHeaderParam( "X-API-KEY", GLOBALS.API_KEY_WEATHER );
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
