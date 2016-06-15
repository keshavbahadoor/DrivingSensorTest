package weather;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import datalayer.StoredPrefsHandler;
import keshav.com.drivingeventlib.ServerRequests;
import keshav.com.restservice.OnTaskComplete;
import keshav.com.utilitylib.DateUtil;
import keshav.com.utilitylib.LogService;

/**
 *  Static methods for parsing weather data
 * Created by Keshav on 2/27/2016.
 */
public class WeatherDataUtil {

    /**
     * Parses a json string using JSONObject then builds and returns a WeatherData
     * object
     * @param rawData json data from response
     * @return WeatherData created object
     */
    public static WeatherData parseJson(String rawData) {

        WeatherData data = new WeatherData();
        try {

            LogService.log( "DATA: " + rawData );
            JSONObject json = new JSONObject( rawData );
            JSONObject weather = (JSONObject) (json.getJSONArray( "weather" )).getJSONObject( 0 );
            JSONObject main = (JSONObject) json.getJSONObject( "main" );
            JSONObject wind = json.getJSONObject( "wind" );

            data.weatherID = weather.getInt( "id" );
            data.main = weather.getString( "main" );
            data.description = weather.getString( "description" );
            data.weatherIcon = weather.getString( "icon" );
            data.city = json.getString( "name" );
            data.temperature = main.getDouble( "temp" );
            data.pressure = main.getDouble( "pressure" );
            data.humidity = main.getDouble( "humidity" );
            data.minTemp = main.getDouble( "temp_min" );
            data.maxTemp = main.getDouble( "temp_max" );
            data.windSpeed = wind.getDouble( "speed" );

            if (json.has( "rain" )) {
                data.rainVolume = json.getJSONObject( "rain" ).getInt( "3h" );
            } else {
                data.rainVolume = 0;
            }
        } catch (JSONException je) {
            LogService.log( "JSON Exception encountered." + je.getMessage());
        } catch ( Exception e ) {
            LogService.log( "Error occurred: " + e.getMessage() );
        }
        return data;
    }

    /**
     * Updates the current locally stored weather data if it is out dated by 2 hours
     * @param latitude
     * @param longitude
     */
    public static void updateWeatherDataIfOutdated(Context context, OnTaskComplete listener, double latitude, double longitude, int hours) {

        WeatherData data = StoredPrefsHandler.retrieveWeatherData( context );
        //LogService.log( data.toString() );
        if (data.date.length() != 0 && DateUtil.dateHasPassed( data.date, hours )) {
            LogService.log( "Updating weather data..." );
            // ServerRequests.getCurrentWeatherData( context, listener, "" + latitude, "" + longitude );
        }
    }

    /**
     * Does post request to update local weather data
     * @param latitude
     * @param longitude
     */
    private static void updateWeatherData(double latitude, double longitude) {



    }


}
