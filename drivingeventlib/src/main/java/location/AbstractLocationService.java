package location;

import android.content.Context;
import android.location.Location;

import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 6/25/2016.
 */
public class AbstractLocationService implements ProjectDrivingLocationServices {

    protected double longitude;
    protected double latitude;
    /**
     * Speed vehicle is moving
     */
    protected float currentSpeed = 0.0F;
    protected float maxSpeed = 0.0F;
    protected float calculateSpeed = 0.0F;
    protected float maxCalculatedSpeed = 0.0F;
    protected long previousTimeStamp = 0L;
    /**
     * Represents current location state
     */
    protected LocationEnum currentLocationState;
    protected LocationEnum previousLocationState;
    protected boolean debugMode = false;
    protected boolean locationServiceAvailable;

    protected Context context;
    /**
     * Represents a geographical location.
     */
    protected Location currentLocation;
    protected Location previousLocation;

    /**
     * Constructor
     */
    public AbstractLocationService()    {
        currentLocationState = LocationEnum.STATIONARY;
        previousLocationState = LocationEnum.STATIONARY;
    }

    /**
     * Updates the speed and sets the current location state depending on the
     * current speed.
     * @param location
     */
    protected void updateSpeedAndLocationState(Location location) {
        try {
            currentSpeed = location.getSpeed();
            if ( currentSpeed <= LocationSettings.IN_VEHICLE_THRESHOLD ) {
                currentLocationState = LocationEnum.STATIONARY;
            }
            if ( currentSpeed > LocationSettings.IN_VEHICLE_THRESHOLD ) {
                currentLocationState = LocationEnum.IN_VEHICLE;
            }
            if ( debugMode ) {
                currentLocationState = LocationEnum.IN_VEHICLE;
            }
            if ( currentSpeed > maxSpeed ) {
                maxSpeed = currentSpeed;
            }
        } catch ( Exception ex ) {
            LogService.log( "Error performing speed and location state update: " + ex.getMessage() );
        }
    }


    /**
     * updates latitude and longitude using the current location
     */
    protected void updateCoordinates() {
        latitude = currentLocation.getLatitude();
        longitude = currentLocation.getLongitude();
    }


    @Override
    public void onStartActions() {

    }

    @Override
    public void onStopActions() {

    }

    @Override
    public void onDestroyActions() {

    }

    @Override
    public double getLatitude() {
        if ( currentLocation != null) {
            return currentLocation.getLatitude();
        }
        return 0.0;
    }

    @Override
    public double getLongitude() {
        if ( currentLocation != null){
            return currentLocation.getLongitude();
        }
        return 0.0;
    }

    @Override
    public double getSpeed() {
        if ( currentLocation != null) {
            return currentLocation.getSpeed();
        }
        return 0.0;
    }

    @Override
    public LocationEnum getLocationState() {
        return currentLocationState;
    }

    @Override
    public void setHighAccuracy() {

    }

    @Override
    public void setLowAccuracy() {

    }

    @Override
    public void setMidAccuracy() {

    }

    public void switchDebugMode() {
        debugMode ^= true;
    }
}
