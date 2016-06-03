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
    private static final long TIME_INTERVAL_GPS_DATA = 2000L;
    /**
     * The time inbetween to check for weather updates. This is in hours
     */
    private static final int WEATHER_UPDATE_TIME_INTERVAL_HOURS = 2;


    public LocationDataTaskDelegator( Context context ) {
        super( context );
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

        if ( locationState == LocationEnum.IN_VEHICLE &&
                (System.currentTimeMillis() - prevTime) > TIME_INTERVAL_GPS_DATA) {

            LogService.log( "Proceeding to capture / store GPS data. " );

            if ( NetworkUtil.isNetworkAvailable( context ) ) {

                WeatherData data = StoredPrefsHandler.retrieveWeatherData( context );

                ServerRequests.postGPSData( context,
                        googleId, "" + latitude, "" + longitude, speed,
                        data.weatherID, data.rainVolume, data.temperature, data.windSpeed,
                        data.pressure, data.humidity
                );
            } else {
                // store data locally
                localStorage.addGPSData( "" + latitude, "" + longitude, speed );
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
