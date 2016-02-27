package keshav.com.drivingeventlib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import MessageEvents.AccelerationDataMessage;
import MessageEvents.GyroDataMessage;
import keshav.com.utilitylib.LogService;
import sensor.lib.LocationEnum;
import sensor.lib.SensorEnum;
import sensor.lib.SensorFilter;

/**
 * Created by Keshav on 2/10/2016.
 */
public class DrivingPatternService extends Service implements SensorEventListener {

    public static final String BROADCAST_ACTION = "DRIVING_PATTERN_SERVICE_ACTION";

    private float SENSOR_SENSITIVITY = 0.5F;
    private long prevTimeStamp = 0L;
    private SensorEnum sensorEnum;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private AccelerationDataMessage accelerationDataMessage;
    private GyroDataMessage gyroDataMessage;
    private LocationEnum locationStatus;

    /**
     * Initializes the following:
     * - SensorManager
     * - Accelerometer, Gyroscope Sensor
     */
    @Override
    public void onCreate() {
        super.onCreate();

        this.sensorManager = (SensorManager) this.getApplicationContext().getSystemService( Context.SENSOR_SERVICE );
        accelerometerSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        gyroscopeSensor = sensorManager.getDefaultSensor( Sensor.TYPE_GYROSCOPE );

        sensorManager.registerListener( this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL );
        sensorManager.registerListener( this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL );

        accelerationDataMessage = new AccelerationDataMessage();
        gyroDataMessage = new GyroDataMessage( );
        locationStatus = LocationEnum.STATIONARY;

        Toast.makeText( this.getApplicationContext(), "Driving Pattern service started", Toast.LENGTH_SHORT ).show();
        LogService.log( "Driving Pattern Service has been created" );
    }


    /**
     * If the sensors are not required after the application is closed,
     * the listener can be unregistered
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sensorManager.unregisterListener( this );
        if ( EventBus.getDefault().isRegistered( this ) ) {
            EventBus.getDefault().unregister( this );
        }
        Toast.makeText( this.getApplicationContext(), "Driving Pattern service has ended", Toast.LENGTH_SHORT ).show();
    }


    /**
     * This can be used in place of the onStart event. onStart event is currently depricated.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        super.onStartCommand( intent, flags, startId );
        if ( !EventBus.getDefault().isRegistered( this ) ) {
            EventBus.getDefault().register( this );
        }
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy ) {

    }


    /**
     * Checks the type of sensor changed event and sends a broadcast with the bundled data.
     * @param event
     */
    @Override
    public void onSensorChanged( SensorEvent event ) {

        if (locationStatus == LocationEnum.IN_VEHICLE) {
            switch ( event.sensor.getType() ) {

                case Sensor.TYPE_ACCELEROMETER:
                    accelerationDataMessage.updateVals( SensorFilter.applyLowPassFilterRounded( SensorFilter.applyHighPassFilter( event.values ) ) );
                    EventBus.getDefault().post( accelerationDataMessage );
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    gyroDataMessage.updateVals( SensorFilter.applyLowPassFilterRounded( SensorFilter.applyHighPassFilter( event.values ) ) );
                    EventBus.getDefault().post( gyroDataMessage );
                    break;
            }
        }
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
    public void handleEvent(Object event) {

    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}
