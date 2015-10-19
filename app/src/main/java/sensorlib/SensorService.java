package sensorlib;

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

/**
 * Created by Keshav on 10/4/2015.
 */
public class SensorService extends Service implements SensorEventListener {

    public static final String BROADCAST_ACTION = "SENSOR_ACTION";

    protected float SENSOR_SENSITIVITY = 0.5F;
    protected long prevTimeStamp = 0L;
    protected SensorEnum sensorEnum;
    protected SensorManager sensorManager;
    protected Sensor accelerometerSensor;


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
        this.accelerometerSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );

        sensorManager.registerListener( this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL );
        Toast.makeText( this.getApplicationContext(), "Sensor service started", Toast.LENGTH_SHORT ).show();
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



        if ( (event.timestamp - prevTimeStamp) / 1000000000.0 > SENSOR_SENSITIVITY) {
            prevTimeStamp = event.timestamp;

            Intent broadcast = new Intent();
            broadcast.setAction( BROADCAST_ACTION );
            broadcast.putExtra( "accelerometer_x", event.values[0] );
            broadcast.putExtra( "accelerometer_y", event.values[1] );
            broadcast.putExtra( "accelerometer_z", event.values[2] );
            this.sendBroadcast( broadcast );

            Log.d( "Sensor Values", "X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2] );
        }
    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}
