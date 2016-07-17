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

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

import datalayer.StoredPrefsHandler;
import datasync.DataSyncAdapter;
import keshav.com.drivingeventlib.GLOBALS;
import keshav.com.utilitylib.LogService;
import location.AbstractLocationService;
import location.AndroidLocationService;
import location.GoogleLocationService;
import location.LocationEnum;
import location.LocationSettings;

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
    private LocationEnum locationState;
    private LocationEnum previousLocationState;
    private AccelerometerDataTaskDelegator accelerometerDataTaskDelegator;
    private LocationDataTaskDelegator locationDataTaskDelegator;
    private AbstractLocationService locationService;

    /**
     * Keeps the last recent detected activities. This is used to filter out false positives.
     * If the list contains all of the same detected activities, then we can assume that the currently
     * detected activity is occurring.
     */
    private List<Integer> detectedActivities;
    private static final int MAX_DETECTED_ACTIVITIES = 3;


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
            // updateLocationState();
            // handle location data
            locationDataTaskDelegator.handleTask( intent );
        }
    };

    /**
     * This is called when the detected activity is changed
     * Origin: ActivityRecognitionService
     */
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            LogService.log( "Activity Recognition Receiver called" );
            updateLocationState( intent.getIntExtra( "activity", DetectedActivity.STILL ) );
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
        //sensorManager.registerListener( this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL );

        // init location related vars
        locationState = LocationEnum.STATIONARY;
        previousLocationState = locationState;

        // get google ID from stored prefs
        googleID = StoredPrefsHandler.getGoogleAccountID( this.getApplicationContext() );

        // Set up broadcast receiver
        registerBroadcastReceivers();

        // Set up detected activities list
        detectedActivities = new ArrayList<>();
        for (int i=0; i<MAX_DETECTED_ACTIVITIES; i++) {
            detectedActivities.add( -1 );
        }

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

        // init location service. Service created can either be basic Android Location service
        // or Google based location service utilizing Fused Location APIs
        // locationService = AndroidLocationService.getInstance( getApplicationContext() );
        locationService = GoogleLocationService.getInstance( getApplicationContext() );
        GLOBALS.locationService = locationService;

        LogService.log( "DRIVING SERVICE CREATED" );
    }

    /**
     * Can be used to rereigster listener
     */
    public void registerBroadcastReceivers() {
        LocalBroadcastManager.getInstance( this.getApplicationContext() )
                .unregisterReceiver( gpsDataReceiver );
        LocalBroadcastManager.getInstance( this.getApplicationContext() )
                .registerReceiver( gpsDataReceiver, new IntentFilter( LocationSettings.BROADCAST_ACTION ) );
        LocalBroadcastManager.getInstance( this.getApplicationContext() )
                .registerReceiver( activityReceiver, new IntentFilter( ActivityRecognitionService.BROADCAST_ACTION ) );
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

        locationService.onDestroyActions();
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

        //locationService.onStartActions();

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
    private void updateLocationState(int activity) {

        LogService.log( "LOCation state ---- " + locationState );

        if ( GLOBALS.DEBUG_ALWAYS_IN_VEHICLE ) {
            locationState = LocationEnum.IN_VEHICLE;
        } else {
            // Add to list of current activities
            addDetectedActivity( activity );

            // use activity recognition instead of location based state:
            if ( !allActivitiesContains( DetectedActivity.IN_VEHICLE ) ) {
                locationService.setHighAccuracy();
                locationState = LocationEnum.IN_VEHICLE;
            } else {
                locationService.onStopActions();
                locationState = LocationEnum.STATIONARY;
            }
        }

        // Update other dependencies
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
            //locationService.setHighAccuracy();

        } else {
            LogService.log( "*** Decreasing detection sensitivity ***" );
            sensorManager.unregisterListener( this );
            //locationService.setLowAccuracy();
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

    /**
     * Adds to the detected activity list.
     * @param activity
     */
    private void addDetectedActivity(int activity) {
        if (detectedActivities.size() >= MAX_DETECTED_ACTIVITIES) {
            detectedActivities.remove( 0 );
        }
        detectedActivities.add( activity );
    }

    /**
     * Checks the current hold of activities against a given activity
     * if all the activities equate the given activity, return true. Else return false.
     * @param activity
     * @return
     */
    private boolean allActivitiesContains(int activity){
        for (int i : detectedActivities){
           if (i != activity){
               return false;
           }
        }
        return true;
    }


}
