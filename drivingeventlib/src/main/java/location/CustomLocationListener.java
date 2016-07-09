package location;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import MessageEvents.GPSDataMessage;
import keshav.com.utilitylib.LogService;

import java.util.Calendar;

/**
 * Created by Keshav on 10/11/2015.
 */
@Deprecated
public class CustomLocationListener implements LocationListener  {


    public static int MY_PERMISSION_ACCESS_COURSE_LOCATION;

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    //The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000;//1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;

    private static CustomLocationListener instance = null;

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
    public LocationEnum previousLocationState;
    public GPSDataMessage dataMessage;

    public float speed = 0.0F;
    public float maxSpeed = 0.0F;
    public float calculateSpeed = 0.0F;
    public float maxCalculatedSpeed = 0.0F;
    private long previousTimeStamp = 0L;

    private boolean debugMode = false;

    /**
     * Singleton implementation
     * @return instance
     */
    public static CustomLocationListener getInstance( Context context)     {
        if (instance == null) {
            instance = new CustomLocationListener(context);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    private CustomLocationListener( Context c )     {

        context = c;
        initLocationService(LocationUpdateDistance.AT_REST, LocationUpdateTime.AT_REST);
        dataMessage = new GPSDataMessage();
        LogService.log("CustomLocationListener created");
    }

    /**
     * Allows clients to change the accuracy of the rate of updates of location service
     * This requires a permission check
     * @param updateTime minimum update time in miliseconds
     * @param updateDistance minimum update distance in meters
     */
    public void changeAccuracy(long updateTime, long updateDistance) {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        if (locationManager != null ){
            locationManager.removeUpdates( this );
            initLocationService( updateDistance, updateTime );

            LogService.log( "Location Changed!" );
        }
    }

    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService(long minUpdateDistance, long minUpdateTime) {

        try   {
            longitude = 0.0;
            latitude = 0.0;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if ( locationManager != null ) {
                // Get GPS and network status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (forceNetwork)
                    isGPSEnabled = false;

                if (!isNetworkEnabled && !isGPSEnabled)    {
                    this.locationServiceAvailable = false;
                }
                //else
                {
                    this.locationServiceAvailable = true;

                    if (isNetworkEnabled) {
                        initiateLocationUpdates( LocationManager.NETWORK_PROVIDER, minUpdateDistance, minUpdateTime);
                    }
                    if (isGPSEnabled)  {
                        initiateLocationUpdates( LocationManager.GPS_PROVIDER, minUpdateDistance, minUpdateTime );
                    }
                    locationState = LocationEnum.STATIONARY;
                    previousLocationState = locationState;
                }
            }
//            else {
//                buildAlertMessageNoGps();
//                initLocationService( context );
//            }
        } catch (Exception ex)  {
            LogService.log( "Error creating location service: " + ex.getMessage() );
        }
    }

    /**
     * requests location updates via the location manage,
     * gets the last known location and updates coordinates.
     *
     * Note: As of Android 6.0, Permissions needs to be checked here
     * in order for location manager operations to be allowed.
     * @param locationProvider can be either gps or network based
     */
    private void initiateLocationUpdates(String locationProvider, long minUpdateDistance, long minUpdateTime) {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        locationManager.requestLocationUpdates(locationProvider, minUpdateTime, minUpdateDistance, this);

        if (locationManager != null)   {
            location = locationManager.getLastKnownLocation(locationProvider);
            updateCoordinates();
        }
    }

    /**
     * Calculates and saves speed
     */
    private void calculateSpeed(Location location) {
        speed = location.getSpeed();

        try {
            // Speed = distance / time in ms^-1
            calculateSpeed = ( location.distanceTo( previousLocation ) / ( Calendar.getInstance().getTimeInMillis() - previousTimeStamp ) * 1000 );
            previousTimeStamp = Calendar.getInstance().getTimeInMillis();

            if ( speed <= LocationSettings.IN_VEHICLE_THRESHOLD ) {
                locationState = LocationEnum.STATIONARY;
            }
            if ( speed > LocationSettings.IN_VEHICLE_THRESHOLD ) {
                locationState = LocationEnum.IN_VEHICLE;
            }
            if ( debugMode ) {
                locationState = LocationEnum.IN_VEHICLE;
            }
            if ( speed > maxSpeed ) {
                maxSpeed = speed;
            }
            if ( calculateSpeed > maxCalculatedSpeed ) {
                maxCalculatedSpeed = calculateSpeed;
            }
        } catch ( Exception ex ) {
            LogService.log( "Error performing CustomLocationListener calculation: " + ex.getMessage() );
        }
    }

    /**
     * Updates the local coorindate variables
     */
    private void updateCoordinates()     {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }



    /**
     * sets previous location, updates coordinates, calculates speed, and sends broadcast
     * @param newLocation
     */
    @Override
    public void onLocationChanged(Location newLocation)     {
        this.previousLocation = this.location;
        this.location = newLocation;
        updateCoordinates();
        calculateSpeed( newLocation );
        dataMessage.updateData( latitude, longitude, speed );

        Intent intent = new Intent(LocationSettings.BROADCAST_ACTION);
        intent.putExtra( "latitude", latitude );
        intent.putExtra( "longitude", longitude );
        intent.putExtra( "speed", speed );
        LocalBroadcastManager.getInstance( context ).sendBroadcast( intent );
        LogService.log( "LOCATION CHANGED" );
    }



    /**
     * Tears down the object
     */
    public void destroy() {
    }

    /**
     * Creates a prompt for the user to enable GPS
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it to proceed?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

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

    public void switchDebugMode() {
        debugMode ^= true;
    }

}
