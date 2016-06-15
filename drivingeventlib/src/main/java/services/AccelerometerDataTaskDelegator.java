package services;

import android.content.Context;
import keshav.com.drivingeventlib.ServerRequests;
import keshav.com.utilitylib.LogService;
import keshav.com.utilitylib.NetworkUtil;
import sensor.lib.SensorFilter;

/**
 * Created by Keshav on 5/21/2016.
 */
public class AccelerometerDataTaskDelegator extends DrivingServiceTaskDelegator {

    /**
     * The time in-between action to be done on accelerometer data
     */
    private static final long TIME_INTERVAL_ACCELEROMETER = 500L;


    public AccelerometerDataTaskDelegator( Context context ) {
        super( context );
    }


    /**---------------------------------------------------------------------------------------------
     * Handles the received accelerometer data values
     * - if we are in the vehicle and adequate time has elapsed, then we handle the data
     * @param values Current raw sensor data
     *--------------------------------------------------------------------------------------------*/
    @Override
    public void handleTask( float[] values ) {

        if (  (System.currentTimeMillis() - prevTime) > TIME_INTERVAL_ACCELEROMETER ) {

            saveAccelerationDataLocally(SensorFilter.applyLowPassFilterRounded(
                                        SensorFilter.applyHighPassFilter( values ) ));

            prevTime = System.currentTimeMillis();
        }
    }

    /**
     * Saves the data to local database instance
     * @param values Current raw sensor data
     */
    private void saveAccelerationDataLocally(float[] values) {
        LogService.log( "Proceeding to store acceleration data. " );
        localStorage.addSensorData( values[0], values[1], values[2] );
    }

    /**
     * If internet access is available, the data is sent to the server.
     * Otherwise it is stored locally.
     * This is an old method - new thought: all data should be stored locally, and only
     * synced when internet access is avaiable
     * @param values Current raw sensor data
     */
    private void handleAccelerationData(float[] values) {
        LogService.log( "Proceeding to capture / store acceleration data. " );
        if ( NetworkUtil.isNetworkAvailable( context ) ) {
            // Add acceleration data using retrofit
        } else {
            saveAccelerationDataLocally(values);
        }
    }
}
