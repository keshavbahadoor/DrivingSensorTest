package services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import MessageEvents.AccelerationDataMessage;
import MessageEvents.GPSDataMessage;
import datalayer.LocalStorage;
import datalayer.StoredPrefsHandler;
import keshav.com.drivingeventlib.ServerRequests;
import keshav.com.drivingeventlib.SyncScheduler;
import keshav.com.restservice.OnTaskComplete;
import keshav.com.utilitylib.DateUtil;
import keshav.com.utilitylib.LogService;
import keshav.com.utilitylib.NetworkUtil;
import sensor.lib.LocationEnum;
import weather.WeatherData;
import weather.WeatherDataUtil;

/**
 * Created by Keshav on 2/20/2016.
 */
@Deprecated
public class DataCacheService extends Service implements OnTaskComplete {

    private static final String LOG_TAG = "DATA_CACHE_SERVICE";
    private static final long  TIME_INTERVAL = 4000L;
    private static final long TIME_INTERVAL_GPS_DATA = 2000L;
    private String googleID;
    private LocationEnum locationStatus;
    private LocalStorage localStorage;
    private SyncScheduler syncScheduler;
    private long prevTime = 0L;
    private long prevTimeGPS = 0L;
    private GPSDataMessage currentGPSData;

    @Override
    public void onCreate() {
        super.onCreate();
        localStorage = LocalStorage.getInstance( this.getApplicationContext() );
        syncScheduler = new SyncScheduler();
        prevTime = System.currentTimeMillis();
        prevTimeGPS = System.currentTimeMillis();
        locationStatus = LocationEnum.STATIONARY;
        googleID = StoredPrefsHandler.getGoogleAccountID( this.getApplicationContext() );
        LogService.log( "DataCacheService has been created" );
        currentGPSData = new GPSDataMessage();
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        super.onStartCommand( intent, flags, startId );
        syncScheduler.setSchedule( this );
        EventBus.getDefault().register( this );
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister( this );
        super.onDestroy();
    }

    /**
     * Receives the current status of location. Based on this, the other functions can take place.
     * @param status
     */
    @Subscribe
    public void onReceiveLocationEnum( LocationEnum status ) {
        locationStatus = status;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    public void onReceiveAccelerometerData( AccelerationDataMessage dataMessage ) {

        if ( locationStatus == LocationEnum.IN_VEHICLE &&
                (System.currentTimeMillis() - prevTime) > TIME_INTERVAL) {

            LogService.log( "Proceeding to capture / store acceleration data. " );
            if ( NetworkUtil.isNetworkAvailable( this.getApplicationContext() ) ) {
                ServerRequests.postAccelerationData( this.getApplicationContext(),
                        googleID,
                        dataMessage.sensorVals[0],
                        dataMessage.sensorVals[1],
                        dataMessage.sensorVals[2] );
            } else {
                // store data locally
                localStorage.addSensorData( dataMessage.sensorVals[0], dataMessage.sensorVals[1], dataMessage.sensorVals[2] );
            }
            prevTime = System.currentTimeMillis();
        }
    }

    /**
     * Ensures that the data received from GPS is stored either locally, or sent to the server.
     * Does the following:
     *      - Checks if the device is in a moving vehicle and that its time to capture data
     *      - Checks if network access is available :
     *              - Fetches weather data
     *              - captures data
     *      - If networks access is unavailable, we just store data locally.
     *          TODO : no weather data captured if network access is unavailable
     * @param dataMessage Data container received from Location Listener
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    public void onReceiveGPSData( GPSDataMessage dataMessage ) {

        LogService.log( "received GPS data" );

        // Update Weather data using received GPS data
        // Note that weather data is only updated every 2 hours
        WeatherData data = StoredPrefsHandler.retrieveWeatherData( this.getApplicationContext() );
        if (data.date.length() == 0 && DateUtil.dateHasPassed( data.date, 0 )) {
            LogService.log( "Updating weather data..." );
            ServerRequests.getCurrentWeatherData( this.getApplicationContext(), this, "" + dataMessage.latitude, "" + dataMessage.longitude );
        }

        if ( locationStatus == LocationEnum.IN_VEHICLE &&
                (System.currentTimeMillis() - prevTimeGPS) > TIME_INTERVAL_GPS_DATA) {
            LogService.log( "Proceeding to capture / store GPS data. " );

            if ( NetworkUtil.isNetworkAvailable( this.getApplicationContext() ) ) {

//                ServerRequests.postGPSData( this.getApplicationContext(),
//                        googleID,
//                        "" + dataMessage.latitude,
//                        "" + dataMessage.longitude,
//                        dataMessage.speed );
            } else {
                // store data locally
                localStorage.addGPSData( "" + dataMessage.latitude, "" + dataMessage.longitude, dataMessage.speed );
            }
            prevTimeGPS = System.currentTimeMillis();
        }

        // Update current data variable
        currentGPSData = dataMessage;
    }

    /**
     * Rest server task completed callback
     * @param result
     */
    @Override
    public void onTaskCompleted( String result ) {
        Log.d(LOG_TAG, "on Task complete callback called");
        StoredPrefsHandler.storeWeatherData( this.getApplicationContext(),  WeatherDataUtil.parseJson( result ) );
    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}
