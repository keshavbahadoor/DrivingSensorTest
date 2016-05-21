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
    private static final long TIME_INTERVAL_ACCELEROMETER = 4000L;


    public AccelerometerDataTaskDelegator( Context context ) {
        super( context );
    }


    /**---------------------------------------------------------------------------------------------
     * Handles the received accelerometer data values
     * - if we are in the vehicle and adequate time has elapsed, then we send the data
     *      to the server depending on network connection.
     *      If no network connectivity, then we store data locally instead.
     * @param values Current raw sensor data
     *--------------------------------------------------------------------------------------------*/
    @Override
    public void handleTask( float[] values ) {

        if (  (System.currentTimeMillis() - prevTime) > TIME_INTERVAL_ACCELEROMETER ) {

            values = SensorFilter.applyLowPassFilterRounded(
                    SensorFilter.applyHighPassFilter( values ) );

            LogService.log( "Proceeding to capture / store acceleration data. " );
            if ( NetworkUtil.isNetworkAvailable( context ) ) {
                ServerRequests.postAccelerationData( context,
                        googleId, values[0], values[1], values[2] );
            } else {
                localStorage.addSensorData( values[0], values[1], values[2] );
            }
            prevTime = System.currentTimeMillis();
        }
    }
}
