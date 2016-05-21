package datalayer;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import weather.WeatherData;

/**
 * Created by Keshav on 2/28/2016.
 */
public class StoredPrefsHandler {

    public static final String WEATHER_DATA = "WEATHER_DATA";
    public static final String CURRENT_ACCOUNT_ID_PREF = "ACCOUNT_ID_NUMBER";

    /**
     * Stores weather data into stored preferences
     * @param data
     */
    public static void storeWeatherData( Context context, WeatherData data) {

        SharedPreferences.Editor editor = context.getSharedPreferences( WEATHER_DATA, Context.MODE_PRIVATE ).edit();
        editor.putString( "icon", data.weatherIcon );
        editor.putInt( "id", data.weatherID );
        editor.putInt( "rain", data.rainVolume );
        editor.putFloat( "pressure", (float) data.pressure );
        editor.putFloat( "humidity", (float) data.humidity );
        editor.putFloat( "temp", (float) data.temperature );
        editor.putFloat( "temp_min", (float) data.maxTemp );
        editor.putFloat( "temp_max", (float) data.minTemp );
        editor.putString( "date", (new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss a" )).format( Calendar.getInstance().getTime() ) );
        editor.apply();
    }

    /**
     * Retrieves weather data. If there is no weather data, retrieves empty WeatherData
     * object
     * @param context
     * @return
     */
    public static WeatherData retrieveWeatherData(Context context) {

        WeatherData data = new WeatherData();

        SharedPreferences prefs = context.getSharedPreferences( WEATHER_DATA, Context.MODE_PRIVATE );
        data.weatherIcon = prefs.getString( "icon", "" );
        data.weatherID = prefs.getInt( "id", 0 );
        data.rainVolume = prefs.getInt( "rain", 0 );
        data.pressure = prefs.getFloat( "pressure", 0F );
        data.humidity = prefs.getFloat( "humidity", 0F );
        data.temperature = prefs.getFloat( "temp", 0F );
        data.maxTemp = prefs.getFloat( "temp_min", 0F );
        data.minTemp = prefs.getFloat( "temp_max", 0F );
        data.date = prefs.getString("date", "");
        return data;
    }

    /**
     * Retrieves google account ID
     * @param context
     * @return
     */
    public static String getGoogleAccountID(Context context) {

        SharedPreferences prefs = context.getSharedPreferences( CURRENT_ACCOUNT_ID_PREF, Context.MODE_PRIVATE );
        return prefs.getString( "AccountID", "" );
    }
}
