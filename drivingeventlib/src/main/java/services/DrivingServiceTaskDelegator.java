package services;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;

import datalayer.LocalStorage;
import datalayer.StoredPrefsHandler;
import sensor.lib.LocationEnum;
import system.TaskDelegator;

/**
 * Abstract class that is used to create Task Delegators for use within the Driving
 * Service. Child delegators may be created for delegating tasks or work to.
 */
public abstract class DrivingServiceTaskDelegator implements TaskDelegator {


    /**
     * Holds the previous time.
     */
    protected long prevTime = 0L;

    /**
     * Google Identifier for the currently logged in user. Required for server use.
     */
    protected String googleId;

    /**
     * reference to application context
     */
    protected Context context;

    /**
     * reference to local storage
     */
    protected LocalStorage localStorage;

    /**
     * Current Location state of the device
     */
    protected LocationEnum locationState;

    /**
     * Constructor method
     */
    public DrivingServiceTaskDelegator(Context context)
    {
        prevTime = System.currentTimeMillis();
        this.context = context;
        this.localStorage = LocalStorage.getInstance( context );
        googleId = StoredPrefsHandler.getGoogleAccountID( context );
        locationState = LocationEnum.STATIONARY;
    }


    @Override
    public void handleTask( SensorEvent event ) {

    }

    @Override
    public void handleTask( Object object ) {

    }

    @Override
    public void handleTask( float[] values ) {

    }

    @Override
    public void handleTask( Intent intent ) {

    }

    /**
     * Updates the current location state of the device.
     * @param state
     */
    public void updateLocationState(LocationEnum state)
    {
        locationState = state;
    }
}
