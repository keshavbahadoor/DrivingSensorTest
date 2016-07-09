package services;


import android.content.Context;
import android.content.Intent;

import datalayer.StoredPrefsHandler;
import keshav.com.drivingeventlib.ServerRequests;
import keshav.com.restservice.OnTaskComplete;
import keshav.com.utilitylib.LogService;
import keshav.com.utilitylib.NetworkUtil;
import location.LocationEnum;
import weather.WeatherData;
import weather.WeatherDataUtil;

/**
 * Handles all tasks on change of location data.
 */
public class LocationDataTaskDelegator extends DrivingServiceTaskDelegator implements OnTaskComplete {

    /**
     * The time in between action to be done on gps data
     */
    private static final long TIME_INTERVAL_GPS_DATA = 3000L;
    /**
     * The time in-between to check for weather updates. This is in hours
     */
    private static final int WEATHER_UPDATE_TIME_INTERVAL_HOURS = 2;

    public WeatherData currentWeatherData;

    /**
     * Constructor
     * @param context
     */
    public LocationDataTaskDelegator( Context context ) {
        super( context );
        currentWeatherData = StoredPrefsHandler.retrieveWeatherData( context );
    }

    /**
     * Handles the passed GPS data contained in the bundled intent. Data originates from the
     * CustomLocaitonListener implementation.
     * @param intent
     */
    @Override
    public void handleTask( Intent intent ) {

        double latitude = intent.getDoubleExtra( "latitude", 0 );
        double longitude = intent.getDoubleExtra( "longitude", 0 );
        float speed = intent.getFloatExtra( "speed", 0F );

        // Update Weather data using received GPS data
        // Note that weather data is only updated every 2 hours
        WeatherDataUtil.updateWeatherDataIfOutdated( context, this, latitude, longitude, -WEATHER_UPDATE_TIME_INTERVAL_HOURS );

        saveLocationDataLocally( latitude, longitude, speed );
    }

    /**
     * If we are in a moving vechile, store the gathered data locally.
     * Last update time should be checked to ensure that the local Database is not flooded
     * @param latitude current latitude loc
     * @param longitude current longitude loc
     * @param speed current speed
     */
    private void saveLocationDataLocally(double latitude, double longitude, float speed) {


        if ( locationState == LocationEnum.IN_VEHICLE &&
                (System.currentTimeMillis() - prevTime) > TIME_INTERVAL_GPS_DATA) {

            currentWeatherData = StoredPrefsHandler.retrieveWeatherData( context );

            LogService.log( "Proceeding to store GPS data." );
            localStorage.addGPSData( "" + latitude, "" + longitude, speed,
                                    currentWeatherData.weatherID,
                                    currentWeatherData.rainVolume,
                                    currentWeatherData.windSpeed,
                                    currentWeatherData.temperature,
                                    currentWeatherData.pressure,
                                    currentWeatherData.humidity);

            prevTime = System.currentTimeMillis();
        }
    }

    /**
     * If internet access is available, send data to server. otherwise, store data locally.
     * This is an old method - new thought: all data should be stored locally, and only
     * synced when internet access is available
     * @param latitude current latitude loc
     * @param longitude current longitude loc
     * @param speed current speed
     */
    private void handleLocationData(double latitude, double longitude, float speed){

        if ( locationState == LocationEnum.IN_VEHICLE &&
                (System.currentTimeMillis() - prevTime) > TIME_INTERVAL_GPS_DATA) {

            LogService.log( "Proceeding to capture / store GPS data. " );

            currentWeatherData = StoredPrefsHandler.retrieveWeatherData( context );
            if ( NetworkUtil.isNetworkAvailable( context ) ) {

                // post gps data using retrofit

            } else {
                // store data locally
                localStorage.addGPSData( "" + latitude, "" + longitude, speed,
                                        currentWeatherData.weatherID,
                                        currentWeatherData.rainVolume,
                                        currentWeatherData.windSpeed,
                                        currentWeatherData.temperature,
                                        currentWeatherData.pressure,
                                        currentWeatherData.humidity);
            }
            prevTime = System.currentTimeMillis();
        }
    }

    /**
     * Rest server task completed callback
     * @param result
     */
    @Override
    public void onTaskCompleted( String result ) {
        LogService.log( "on Task complete callback called" );
        StoredPrefsHandler.storeWeatherData( context, WeatherDataUtil.parseJson( result ) );
    }
}
