package location;

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
import android.support.v4.content.LocalBroadcastManager;

import keshav.com.utilitylib.LogService;

/**
 * Handles location services using default Android implementation
 * Created by Keshav on 6/25/2016.
 */
public class AndroidLocationService extends AbstractLocationService implements LocationListener {

    private static AndroidLocationService instance = null;
    private final static boolean forceNetwork = false;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

    /**
     * Local constructor
     */
    private AndroidLocationService( Context c )     {
        context = c;
        LogService.log("AndroidLocationService created");
    }

    /**
     * Singleton implementation
     * @return instance
     */
    public static AndroidLocationService getInstance( Context context)     {
        if (instance == null) {
            instance = new AndroidLocationService(context);
        }
        return instance;
    }

    @Override
    public void onStartActions() {
        initLocationService(LocationUpdateDistance.AT_REST, LocationUpdateTime.AT_REST);
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
                    currentLocationState = LocationEnum.STATIONARY;
                    previousLocationState = currentLocationState;
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
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            updateCoordinates();
        }
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

    @Override
    public void setHighAccuracy() {
        changeAccuracy( LocationUpdateTime.AGGRESSIVE, LocationUpdateDistance.AGGRESSIVE );
    }

    @Override
    public void setLowAccuracy() {
        changeAccuracy( LocationUpdateTime.AT_REST, LocationUpdateDistance.AT_REST );
    }

    @Override
    public void setMidAccuracy() {
        changeAccuracy( LocationUpdateTime.NORMAL, LocationUpdateDistance.NORMAL );
    }

    @Override
    public void onLocationChanged( Location newLocation ) {
        previousLocation = currentLocation;
        currentLocation = newLocation;
        updateCoordinates();
        updateSpeedAndLocationState( newLocation );

        Intent intent = new Intent(LocationSettings.BROADCAST_ACTION);
        intent.putExtra( "latitude", latitude );
        intent.putExtra( "longitude", longitude );
        intent.putExtra( "speed", currentSpeed );
        LocalBroadcastManager.getInstance( context ).sendBroadcast( intent );
        LogService.log( "LOCATION CHANGED" );
    }

    @Override
    public void onStatusChanged( String provider, int status, Bundle extras ) {

    }

    @Override
    public void onProviderEnabled( String provider ) {

    }

    @Override
    public void onProviderDisabled( String provider ) {

    }
}
