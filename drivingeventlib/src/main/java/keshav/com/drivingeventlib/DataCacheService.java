package keshav.com.drivingeventlib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import MessageEvents.AccelerationDataMessage;
import MessageEvents.GPSDataMessage;
import keshav.com.utilitylib.LogService;
import keshav.com.utilitylib.NetworkUtil;
import sensor.lib.LocationEnum;

/**
 * Created by Keshav on 2/20/2016.
 */
public class DataCacheService extends Service {

    private static final long  TIME_INTERVAL = 4000L;
    private static final long TIME_INTERVAL_GPS_DATA = 2000L;
    public static final String CURRENT_ACCOUNT_ID_PREF = "ACCOUNT_ID_NUMBER";
    private String googleID;
    private LocationEnum locationStatus;
    private LocalStorage localStorage;
    private SyncScheduler syncScheduler;
    private long prevTime = 0L;
    private long prevTimeGPS = 0L;


    @Override
    public void onCreate() {
        super.onCreate();
        localStorage = LocalStorage.getInstance( this.getApplicationContext() );
        syncScheduler = new SyncScheduler();
        prevTime = System.currentTimeMillis();
        prevTimeGPS = System.currentTimeMillis();
        locationStatus = LocationEnum.STATIONARY;

        SharedPreferences prefs = this.getApplicationContext().getSharedPreferences( CURRENT_ACCOUNT_ID_PREF, Context.MODE_PRIVATE );
        googleID = prefs.getString( "AccountID", "" );
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        super.onStartCommand( intent, flags, startId );
        syncScheduler.setSchedule( this );
        if ( !EventBus.getDefault().isRegistered( this ) ) {
            EventBus.getDefault().register( this );
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if ( EventBus.getDefault().isRegistered( this ) ) {
            EventBus.getDefault().unregister( this );
        }
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

    @Subscribe
    public void onReceiveAccelerometerData( AccelerationDataMessage dataMessage ) {

        if ( locationStatus == LocationEnum.IN_VEHICLE &&
                (System.currentTimeMillis() - prevTime) > TIME_INTERVAL) {

            LogService.log( "Proceeding to capture / store acceleration data. " );
            if ( NetworkUtil.isNetworkAvailable( this.getApplicationContext() ) ) {
                ServerRequests.postAccelerationData( this.getApplicationContext(),
                        googleID,
                        dataMessage.sensorVals[0],
                        dataMessage.sensorVals[1],
                        dataMessage.sensorVals[2]);
            } else {
                // store data locally
                localStorage.addSensorData( dataMessage.sensorVals[0], dataMessage.sensorVals[1], dataMessage.sensorVals[2] );
            }
            prevTime = System.currentTimeMillis();
        }
    }

    @Subscribe
    public void onReceiveGPSData( GPSDataMessage dataMessage ) {

        if ( locationStatus == LocationEnum.IN_VEHICLE &&
                (System.currentTimeMillis() - prevTimeGPS) > TIME_INTERVAL_GPS_DATA) {

            LogService.log( "Proceeding to capture / store GPS data. " );
            if ( NetworkUtil.isNetworkAvailable( this.getApplicationContext() ) ) {
                ServerRequests.postGPSData( this.getApplicationContext(),
                        googleID,
                        dataMessage.latitude,
                        dataMessage.longitude,
                        dataMessage.speed );
            } else {
                // store data locally
                localStorage.addGPSData( dataMessage.latitude, dataMessage.longitude, dataMessage.speed );
            }
            prevTimeGPS = System.currentTimeMillis();
        }
    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}
