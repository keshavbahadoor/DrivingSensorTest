package sensor.lib;

import android.hardware.Sensor;

/**
 * Created by Keshav on 10/4/2015.
 */
@Deprecated
public class AccelerometerSensor extends SensorTemplate {

    public AccelerometerSensor() {
        super();
        this.sensorName = "Accelerometer";
        this.sensorType = Sensor.TYPE_ACCELEROMETER;
        this.sensorEnum = SensorEnum.ACCELEROMETER;
    }

    @Override
    public void onSensorChangedHook( float[] values ) {

    }
}
