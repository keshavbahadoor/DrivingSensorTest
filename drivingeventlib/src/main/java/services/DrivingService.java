package services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import MessageEvents.AccelerationDataMessage;
import datalayer.LocalStorage;
import datalayer.StoredPrefsHandler;
import keshav.com.drivingeventlib.ServerRequests;
import keshav.com.restservice.OnTaskComplete;
import keshav.com.utilitylib.DateUtil;
import keshav.com.utilitylib.LogService;
import keshav.com.utilitylib.NetworkUtil;
import sensor.lib.CustomLocationListener;
import sensor.lib.LocationEnum;
import sensor.lib.SensorFilter;
import weather.WeatherData;
import weather.WeatherDataUtil;

/**
 * MAIN BACKGROUND SERVICE
 * Created by Keshav on 3/1/2016.
 */
public class DrivingService extends Service implements SensorEventListener, OnTaskComplete {

    /**
     * The time inbetween action to be done on accelerometer values
     */
    private static final long TIME_INTERVAL_ACCELEROMETER = 4000L;

    /**
     * The time in between action to be done on gps data
     */
    private static final long TIME_INTERVAL_GPS_DATA = 2000L;
    /**
     * The time inbetween to check for weather updates. This is in hours
     */
    private static final int WEATHER_UPDATE_TIME_INTERVAL_HOURS = 2;

    private long prevTime = 0L;
    private long prevTimeGPS = 0L;

    /**
     * Google Identifier for the currently logged in user. Required for server use.
     */
    private String googleID;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private CustomLocationListener customLocationListener;
    private LocalStorage localStorage;
    private LocationEnum locationState;
    private AccelerometerDataTaskDelegator accelerometerDataTaskDelegator;

    /**
     * This is called when location data is changed.
     */
    private BroadcastReceiver gpsDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            LogService.log( "GPS broadcast receiver called" );
            locationState = customLocationListener.locationState;
            handleGPSData(  intent.getDoubleExtra( "latitude", 0 ),
                    intent.getDoubleExtra( "longitude", 0 ),
                    intent.getFloatExtra( "speed", 0F )
            );
        }
    };

    /**
     * Initializes the following:
     * - SensorManager
     * - Accelerometer, Gyroscope Sensor
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // init Task Handlers
        accelerometerDataTaskDelegator = new AccelerometerDataTaskDelegator( this.getApplicationContext() );

        // init sensor related vars
        sensorManager = (SensorManager) this.getApplicationContext().getSystemService( Context.SENSOR_SERVICE );
        accelerometerSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        sensorManager.registerListener( this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL );

        // init location related vars
        localStorage = LocalStorage.getInstance( this.getApplicationContext() );
        prevTime = System.currentTimeMillis();
        prevTimeGPS = System.currentTimeMillis();
        locationState = LocationEnum.STATIONARY;

        // get google ID from stored prefs
        googleID = StoredPrefsHandler.getGoogleAccountID( this.getApplicationContext() );

        // Set up broadcast receiver
        LocalBroadcastManager.getInstance( this.getApplicationContext() )
                .registerReceiver( gpsDataReceiver, new IntentFilter( CustomLocationListener.BROADCAST_ACTION ) );

        LogService.log( "DRIVING SERVICE CREATED" );
    }

    /**
     * If the sensors are not required after the application is closed,
     * the listener can be unregistered
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sensorManager.unregisterListener( this );
        LocalBroadcastManager.getInstance( this.getApplicationContext() ).unregisterReceiver( gpsDataReceiver );
        customLocationListener.destroy();
        LogService.log( "Driving Service has ended" );
        Toast.makeText( this.getApplicationContext(), "Driving Service has ended", Toast.LENGTH_SHORT ).show();
    }


    /**
     * This can be used in place of the onStart event. onStart event is currently deprecated.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        super.onStartCommand( intent, flags, startId );
        customLocationListener = CustomLocationListener.getLocationManager( this.getApplicationContext() );
        return START_STICKY;
    }


    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy ) {



    }

    /**
     * Fires each time the sensor value changes.
     * Only handle the data if we are currently in a moving vehicle
     * @param event
     */
    @Override
    public void onSensorChanged( SensorEvent event ) {

        if ( locationState == LocationEnum.IN_VEHICLE) {
            switch ( event.sensor.getType() ) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometerDataTaskDelegator.handleTask( event.values );
                    break;
            }
        }
    }

    /**---------------------------------------------------------------------------------------------------------------------
     * Handles the received accelerometer data values
     * - if we are in the vehicle and adequate time has elapsed, then we send the data
     *      to the server depending on network connection.
     *      If no network connectivity, then we store data locally instead.
     * @param vals Current raw accelerometer data
     *--------------------------------------------------------------------------------------------------------------------*/
    public void handleAccelerometerData( float[] vals ) {

        if (  (System.currentTimeMillis() - prevTime) > TIME_INTERVAL_ACCELEROMETER ) {

            LogService.log( "Proceeding to capture / store acceleration data. " );
            if ( NetworkUtil.isNetworkAvailable( this.getApplicationContext() ) ) {
                ServerRequests.postAccelerationData( this.getApplicationContext(),
                        googleID, vals[0], vals[1], vals[2] );
            } else {
                localStorage.addSensorData( vals[0], vals[1], vals[2] );
            }
            prevTime = System.currentTimeMillis();
        }
    }

    /**---------------------------------------------------------------------------------------------------------------------
     * Handles the passed GPS data
     * @param latitude current location lat
     * @param longitude current location lon
     * @param speed current speed of the vehicle
     *--------------------------------------------------------------------------------------------------------------------*/
    public void handleGPSData( double latitude, double longitude, float speed ) {

        // Update Weather data using received GPS data
        // Note that weather data is only updated every 2 hours
       WeatherDataUtil.updateWeatherDataIfOutdated( getApplicationContext(), this, latitude, longitude, -WEATHER_UPDATE_TIME_INTERVAL_HOURS );

        if ( locationState == LocationEnum.IN_VEHICLE &&
                (System.currentTimeMillis() - prevTimeGPS) > TIME_INTERVAL_GPS_DATA) {

            LogService.log( "Proceeding to capture / store GPS data. " );

            if ( NetworkUtil.isNetworkAvailable( this.getApplicationContext() ) ) {

                WeatherData data = StoredPrefsHandler.retrieveWeatherData( getApplicationContext() );

                ServerRequests.postGPSData( this.getApplicationContext(),
                        googleID, "" + latitude, "" + longitude, speed,
                        data.weatherID, data.rainVolume, data.temperature, data.windSpeed,
                        data.pressure, data.humidity
                );
            } else {
                // store data locally
                localStorage.addGPSData( "" + latitude, "" + longitude, speed );
            }
            prevTimeGPS = System.currentTimeMillis();
        }
    }


    /**
     * Rest server task completed callback
     * @param result
     */
    @Override
    public void onTaskCompleted( String result ) {
        LogService.log( "on Task complete callback called" );
        StoredPrefsHandler.storeWeatherData( this.getApplicationContext(), WeatherDataUtil.parseJson( result ) );
    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}
