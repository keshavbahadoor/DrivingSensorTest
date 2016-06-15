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
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import datalayer.StoredPrefsHandler;
import datasync.DataSyncAdapter;
import keshav.com.drivingeventlib.GLOBALS;
import keshav.com.utilitylib.LogService;
import location.CustomLocationListener;
import location.GoogleLocationService;
import location.LocationEnum;
import location.LocationSettings;
import location.LocationUpdateDistance;
import location.LocationUpdateTime;

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
    private GoogleLocationService googleLocationService;
    private LocationEnum locationState;
    private LocationEnum previousLocationState;
    private AccelerometerDataTaskDelegator accelerometerDataTaskDelegator;
    private LocationDataTaskDelegator locationDataTaskDelegator;

    // Storage for an instance of the sync adapter
    private static DataSyncAdapter dataSyncAdapter = null;

    // For use as a tread safe lock
    private static final Object sSyncAdapterLock = new Object();

    IBinder mBinder = new LocalBinder();


    /**
     * This is called when location data is changed.
     * Origin: CustomLocationListener
     */
    private BroadcastReceiver gpsDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            LogService.log( "GPS broadcast receiver called" );

            updateLocationState();



            // handle location data
            locationDataTaskDelegator.handleTask( intent );
        }
    };


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public DrivingService getService() {
            return DrivingService.this;
        }
    }


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
        previousLocationState = locationState;

        // get google ID from stored prefs
        googleID = StoredPrefsHandler.getGoogleAccountID( this.getApplicationContext() );

        // Set up broadcast receiver
        registerBroadcastReceiver();

        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (sSyncAdapterLock) {
            if (dataSyncAdapter == null) {
                dataSyncAdapter = new DataSyncAdapter(getApplicationContext(), true);
            }
        }

        // init google location handler
        //googleLocationService = GoogleLocationService.getInstance( getApplicationContext() );

        LogService.log( "DRIVING SERVICE CREATED" );
    }

    /**
     * Can be used to rereigster listener
     */
    public void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance( this.getApplicationContext() )
                .unregisterReceiver( gpsDataReceiver );
        LocalBroadcastManager.getInstance( this.getApplicationContext() )
                .registerReceiver( gpsDataReceiver, new IntentFilter( LocationSettings.BROADCAST_ACTION ) );
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

        //customLocationListener.destroy();
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
        customLocationListener = CustomLocationListener.getInstance( this.getApplicationContext() );
        //googleLocationService.onStartActions();

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

    /**
     * Updates the current location state based on what is received by
     * the CustomLocationListener object.
     * If DEBUG_ALWAYS_IN_VEHICLE is true, we send a false location state of always in vehicle.
     */
    private void updateLocationState() {

        LogService.log( "LOCation state ---- " + locationState );

        if ( GLOBALS.DEBUG_ALWAYS_IN_VEHICLE ) {
            locationState = LocationEnum.IN_VEHICLE;
        } else {
            locationState = customLocationListener.locationState;
            //locationState = googleLocationService.getLocationState();
        }

        // Update other dependancies
        accelerometerDataTaskDelegator.updateLocationState( locationState );
        locationDataTaskDelegator.updateLocationState( locationState );

        if ( locationState != previousLocationState ) {
            handleDynamicGPSandAcceleration();
            previousLocationState = locationState;
        }
    }

    /**
     * Handles tasks on location state
     * this does dynamic acceleration listener actions
     */
    private void handleDynamicGPSandAcceleration() {
        if ( locationState == LocationEnum.IN_VEHICLE ) {
            LogService.log( "*** Increasing detection sensitivity ***" );
            sensorManager.registerListener( this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST );
            customLocationListener.changeAccuracy( LocationUpdateTime.AGGRESSIVE,
                                                   LocationUpdateDistance.AGGRESSIVE );

        } else {
            LogService.log( "*** Decreasing detection sensitivity ***" );
            sensorManager.unregisterListener( this );
            customLocationListener.changeAccuracy( LocationUpdateTime.AT_REST,
                    LocationUpdateDistance.AT_REST );
        }
    }


    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     *
     * TODO ... now being used for binder
     */
    @Override
    public IBinder onBind( Intent intent ) {
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
         return dataSyncAdapter.getSyncAdapterBinder();
        //return mBinder;
    }


}
