package location;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 6/1/2016.
 */
public class GoogleLocationService extends AbstractLocationService implements GoogleApiClient.ConnectionCallbacks,
                                              GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private long interval = LocationSettings.SLOW_INTERVAL;
    private long fastestInterval = LocationSettings.SLOW_INTERVAL;
    private int locationPriority = LocationRequest.PRIORITY_LOW_POWER;
    private static GoogleLocationService instance = null;

    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient googleApiClient;

    /**
     * Defines the quality of service of gathering location data
     */
    private LocationRequest locationRequest;


    /**
     * Constructor:
     *  - Sets up google api client
     */
    private GoogleLocationService(Context context) {
        this.context = context;
        this.buildGoogleApiClient();
    }

    /**
     * Singleton implementation
     * @return instance
     */
    public static GoogleLocationService getInstance(Context context)     {
        if (instance == null) {
            instance = new GoogleLocationService(context);
        }
        return instance;
    }

    /**
     * Initializes the google api client object.
     */
    public synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder( this.context )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
    }

    /**
     * Builds the location request object.
     */
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval( interval );
        locationRequest.setFastestInterval( fastestInterval );
        locationRequest.setPriority( locationPriority );
    }

    /**
     * Allows clients to change the accuracy of the fused location services.
     * Location needs to be re-requested
     * This is done by disconnecting the google API client, and then reconnecting.
     * This allows onConnected event to be called.
     */
    public void changeAccuracy(long interval, long fastestInterval, int priority) {

        this.interval = interval;
        this.fastestInterval = fastestInterval;
        this.locationPriority = priority;

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates( googleApiClient, this );
            googleApiClient.reconnect();
        } else {
            googleApiClient.connect();
        }
    }

    @Override
    public void setHighAccuracy() {
        changeAccuracy( LocationSettings.FAST_INTERVAL,
                    LocationSettings.FAST_INTERVAL,
                    LocationRequest.PRIORITY_HIGH_ACCURACY );
    }

    @Override
    public void setLowAccuracy() {
        changeAccuracy( LocationSettings.SLOW_INTERVAL,
                    LocationSettings.SLOW_INTERVAL,
                    LocationRequest.PRIORITY_LOW_POWER );
    }

    @Override
    public void setMidAccuracy() {
        changeAccuracy( LocationSettings.MEDIUM_INTERVAL,
                    LocationSettings.MEDIUM_INTERVAL,
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
    }

    /**
     * This should be called on applicaion start event
     */
    @Override
    public void onStartActions() {
        LogService.log( "Google Location services - onStartActions" );
        googleApiClient.connect();
        if (googleApiClient.isConnecting()) {
            LogService.log( "connecting.." );
        }
        if (googleApiClient.isConnected()){
            LogService.log( "connected" );
        }
    }

    /**
     * This should be called on application stop event
     */
    @Override
    public void onStopActions(){
        if ( googleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates( googleApiClient, this );
            googleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected( @Nullable Bundle bundle ) {
        LogService.log( "--- INTERVAL : " + interval + " FASTEST INTERVAL : " + fastestInterval );
        createLocationRequest();
        requestLocationUpdates();
        LogService.log( "Google Location Service initiated." );
    }

    /**
     * Requests location updates. Requires the checking of permissions prior
     */
    private void requestLocationUpdates() {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates( googleApiClient, locationRequest, this );
    }


    /**
     * Lets everyone know of our updated latitude, longitude and speed values.
     * This implementation uses a general broadcast.
     */
    private void updateListeners() {
        Intent intent = new Intent(LocationSettings.BROADCAST_ACTION);
        intent.putExtra( "latitude", latitude );
        intent.putExtra( "longitude", longitude );
        intent.putExtra( "speed", currentSpeed );
        LocalBroadcastManager.getInstance( context ).sendBroadcast( intent );

        LogService.log( "Location broadcast sent" );
    }

    /**
     * Calls each time the location has been changed.
     * @param location new location
     */
    @Override
    public void onLocationChanged( Location location ) {

        if (location == null) {
            return;
        }
        currentLocation = location;
        updateCoordinates();
//        LogService.log( "LOCATION: " + location.getLatitude() );
//        LogService.log( "LOCATION: " + location.getLongitude() );
//        LogService.log( "SPEED: " + location.getSpeed() );
        LogService.log( "Location changed" );
        updateSpeedAndLocationState(location);
        updateListeners();
    }

    @Override
    public void onConnectionSuspended( int i ) {
        googleApiClient.connect();
        LogService.log( "Google API Client has been suspended" );
    }

    @Override
    public void onConnectionFailed( @NonNull ConnectionResult connectionResult ) {
        LogService.log("Google services connection failed: " + connectionResult.getErrorCode());
    }

}
