package sensorlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import util.LogService;
import com.driving.senor.test.R;

import java.util.Calendar;

import keshav.com.restservice.HTTPRestTask;
import keshav.com.restservice.RequestType;

/**
 * Created by Keshav on 10/11/2015.
 */
public class LocationService implements LocationListener  {


    public static final String BROADCAST_ACTION = "LOCATION_CHANGED";
    /**
     * in ms^-1 (approx. 20 kmp/h)
     */
    public static final float IN_VEHICLE_THRESHHOLD = 5.5555F;

    public static int MY_PERMISSION_ACCESS_COURSE_LOCATION;

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    //The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 30000;//1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;

    private static LocationService instance = null;

    private static Context context;

    private LocationManager locationManager;
    public Location location;
    private Location previousLocation;
    public double longitude;
    public double latitude;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean locationServiceAvailable;
    public LocationEnum locationState;

    public float speed = 0.0F;
    public float maxSpeed = 0.0F;
    public float calculateSpeed = 0.0F;
    public float maxCalculatedSpeed = 0.0F;
    private long previousTimeStamp = 0L;

    /**
     * Singleton implementation
     * @return
     */
    public static LocationService getLocationManager(Context context)     {
        if (instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    private LocationService( Context c )     {

        initLocationService(c);
        context = c;
        LogService.log("LocationService created");
    }

    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService(Context context) {

        // New requirements for Android M
        if ( Build.VERSION.SDK_INT >= 23 &&
             ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
             ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try   {
            longitude = 0.0;
            latitude = 0.0;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if ( locationManager != null ) {

                // Get GPS and network status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (forceNetwork) isGPSEnabled = false;

                if (!isNetworkEnabled && !isGPSEnabled)    {
                    // cannot get location
                    this.locationServiceAvailable = false;
                }
                //else
                {
                    this.locationServiceAvailable = true;

                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null)   {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            updateCoordinates();
                        }
                    }//end if
                    if (isGPSEnabled)  {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null)  {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateCoordinates();
                        }
                    }
                    locationState = LocationEnum.STATIONARY;
                }
            }
        } catch (Exception ex)  {
            LogService.log( "Error creating location service: " + ex.getMessage() );
        }
    }

    /**
     * Calculates and saves speed
     * @return
     */
    private void calculateSpeed(Location location) {
        speed = location.getSpeed();

        try {
            // Speed = distance / time in ms^-1
            calculateSpeed = ( location.distanceTo( previousLocation ) / ( Calendar.getInstance().getTimeInMillis() - previousTimeStamp ) * 1000 );
            previousTimeStamp = Calendar.getInstance().getTimeInMillis();

            if ( speed <= IN_VEHICLE_THRESHHOLD ) {
                locationState = LocationEnum.STATIONARY;
            }
            if ( speed > IN_VEHICLE_THRESHHOLD ) {
                locationState = LocationEnum.IN_VEHICLE;
            }
            if ( speed > maxSpeed ) {
                maxSpeed = speed;
            }
            if ( calculateSpeed > maxCalculatedSpeed ) {
                maxCalculatedSpeed = calculateSpeed;
            }
        } catch ( Exception ex ) {
            LogService.log( "Error performing LocationService calculation: " + ex.getMessage() );
        }
    }


    private void updateCoordinates()     {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public void onLocationChanged(Location newLocation)     {
        this.previousLocation = this.location;
        this.location = newLocation;
        updateCoordinates();
        calculateSpeed(newLocation );
        sendData();

        Intent broadcast = new Intent();
        broadcast.setAction( BROADCAST_ACTION );
        context.sendBroadcast( broadcast );
    }

    /**
     * Packages and sends data to the server
     */
    private void sendData() {

        HTTPRestTask task = new HTTPRestTask( this.context, RequestType.POST );
        task.setURL( context.getString( R.string.SERVER ) + "addlocationdata" );
        task.addHeaderParam( "X-API-KEY", context.getString( R.string.ApiKey ));
        task.addBodyParam( "latitude", this.latitude );
        task.addBodyParam( "longitude", this.longitude );
        task.addBodyParam( "speed", String.format("%.4f", (speed * 3.6)));
        task.execute(  );
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)     {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean isLocationServiceAvailable() {
        return locationServiceAvailable;
    }

    public boolean isNetworkEnabled() {
        return isNetworkEnabled;
    }

    public boolean isGPSEnabled() {
        return isGPSEnabled;
    }

}
