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
public class DrivingService extends Service implements SensorEventListener  {

    /**
     * Google Identifier for the currently logged in user. Required for server use.
     */
    private String googleID;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private CustomLocationListener customLocationListener;
    private LocationEnum locationState;
    private AccelerometerDataTaskDelegator accelerometerDataTaskDelegator;
    private LocationDataTaskDelegator locationDataTaskDelegator;

    /**
     * This is called when location data is changed.
     * Origin: CustomLocationListener
     */
    private BroadcastReceiver gpsDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            LogService.log( "GPS broadcast receiver called" );
            locationState = customLocationListener.locationState;

            // handle location data
            locationDataTaskDelegator.handleTask( intent );

            // Update location state to all
            accelerometerDataTaskDelegator.updateLocationState( locationState );
            locationDataTaskDelegator.updateLocationState( locationState );
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
        locationDataTaskDelegator = new LocationDataTaskDelegator( this.getApplicationContext() );

        // init sensor related vars
        sensorManager = (SensorManager) this.getApplicationContext().getSystemService( Context.SENSOR_SERVICE );
        accelerometerSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        sensorManager.registerListener( this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL );

        // init location related vars
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
     * Only handle the data if we are currently in a moving vehicle.
     * We delegate the accelerometer tasks to a handler. 
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






    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}
