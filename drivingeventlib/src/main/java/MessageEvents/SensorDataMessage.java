package MessageEvents;


/**
 * This is used to hold sensor data.
 * Used as a message event object that is part of communication process.
 *
 * GreenRobot EventBus will be used to facilitate inter-process communication.
 * Created by Keshav on 2/20/2016.
 */
public class SensorDataMessage {

    public float[] sensorVals;
    public String sensorName;

    public SensorDataMessage(String name) {
        sensorVals = new float[] {0F, 0F, 0F, 0F, 0F, 0F};
        sensorName = name;
    }

    public void updateVals(float [] vals) {
        this.sensorVals = vals;
    }

    public void updateVals(float x, float y, float z) {
        sensorVals[0] = x;
        sensorVals[1] = y;
        sensorVals[2] = z;
    }
}
