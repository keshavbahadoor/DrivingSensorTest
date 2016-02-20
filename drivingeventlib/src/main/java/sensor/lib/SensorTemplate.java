package sensor.lib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Keshav on 10/4/2015.
 */
@Deprecated
public abstract class SensorTemplate extends Fragment implements SensorEventListener
{
    protected float SENSOR_SENSITIVITY = 0.5F;
    protected float DB_INTERVAL = 2F;
    protected long prevTimeStamp = 0L;
    protected long prevDBTimeStamp = 0L;
    protected SensorEnum sensorEnum;
    protected String sensorName;
    protected boolean registered = false;
    protected SensorManager sensorManager;
    protected Sensor sensor;
    protected int sensorType;

    /**
     * Constructor method
     */
    public SensorTemplate()     {
        this.sensorName = "Sensor";
        this.sensorEnum = SensorEnum.DEFAULT;
    }

    /**
     * Registers the sensor with the sensor manager
     */
    public void sensorStart() {

        if (this.sensor != null) {
            sensorManager.registerListener( this, sensor, SensorManager.SENSOR_DELAY_NORMAL ); // TODO research this setting
            Toast.makeText(getActivity(), this.sensorName + " Found", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), this.sensorName + " Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates sensor manager and other related instances
     * @param savedInstanceState
     */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        this.sensorManager = (SensorManager) this.getActivity().getSystemService( Context.SENSOR_SERVICE );
        this.sensor = sensorManager.getDefaultSensor( this.sensorType );
    }

    @Override
    public void onStop() {
        super.onStop();
        this.sensorManager.unregisterListener( this );
    }

    @Override
    public void onSensorChanged( SensorEvent event ) {

        if ( (event.timestamp - prevTimeStamp) / 1000000000.0 > SENSOR_SENSITIVITY) {
            prevTimeStamp = event.timestamp;
            this.onSensorChangedHook( event.values );

            Log.d( "Sensor Values", "X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2] );
        }
    }

    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy ) {

    }

    /**
     * Hook method - template design pattern
     *  - this can be overridden in child to use the sensor values passed
     * @param values
     */
    public abstract void onSensorChangedHook(float[] values);
}
