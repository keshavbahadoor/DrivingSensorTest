package services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import sensor.lib.SensorEnum;
import sensor.lib.SensorFilter;

/**
 * Created by Keshav on 10/4/2015.
 */
@Deprecated
public class SensorService extends Service implements SensorEventListener {

    public static final String BROADCAST_ACTION = "SENSOR_ACTION";

    private float SENSOR_SENSITIVITY = 0.5F;
    private long prevTimeStamp = 0L;
    private SensorEnum sensorEnum;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private Sensor rotationSensor;
    private Sensor gravitySensor;

    /**
     * Constructor
     */
    public SensorService() {
    }

    /**
     * Creates sensor manager and other related instances
     */
    @Override
    public void onCreate() {
        super.onCreate();

        this.sensorManager = (SensorManager) this.getApplicationContext().getSystemService( Context.SENSOR_SERVICE );

        // init sensors
        accelerometerSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        gyroscopeSensor = sensorManager.getDefaultSensor( Sensor.TYPE_GYROSCOPE );
        rotationSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ROTATION_VECTOR );
        gravitySensor = sensorManager.getDefaultSensor( Sensor.TYPE_GRAVITY );

        sensorManager.registerListener( this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL );
        sensorManager.registerListener( this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL );
        sensorManager.registerListener( this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL );
        sensorManager.registerListener( this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL );

        Toast.makeText( this.getApplicationContext(), "Sensor service started", Toast.LENGTH_SHORT ).show();

        Log.d( "SENSOR_SERVICE", "Accelerometer Max: " + accelerometerSensor.getMaximumRange() );
        Log.d( "SENSOR_SERVICE", "Gyroscope Max: " + gyroscopeSensor.getMaximumRange() );
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        super.onStartCommand( intent, flags, startId );

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sensorManager.unregisterListener( this );
    }

    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy ) {

    }

    @Override
    public void onSensorChanged( SensorEvent event ) {
       // if ( (event.timestamp - prevTimeStamp) / 1000000000.0 > SENSOR_SENSITIVITY) {
            prevTimeStamp = event.timestamp;

            Intent broadcast = new Intent();
            broadcast.setAction( BROADCAST_ACTION );

            switch ( event.sensor.getType()  ) {

                case Sensor.TYPE_ACCELEROMETER:
                    broadcast.putExtra( "accelerometer", SensorFilter.applyLowPassFilterRounded( SensorFilter.applyHighPassFilter( event.values ) ) );
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    broadcast.putExtra( "gyroscope", SensorFilter.applyLowPassFilterRounded( event.values ) );
                    break;

                case Sensor.TYPE_ROTATION_VECTOR:
                    broadcast.putExtra( "rotation", SensorFilter.applyLowPassFilterRounded( event.values) );
                    break;

                case Sensor.TYPE_GRAVITY:
                    broadcast.putExtra( "gravity", SensorFilter.applyLowPassFilterRounded( event.values) );
                    break;
            }

            this.sendBroadcast( broadcast );
            // Log.d( "Sensor Values", "X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2] );
       // }
    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}
