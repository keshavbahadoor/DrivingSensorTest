package location;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 6/1/2016.
 */
public class GoogleLocationService implements GoogleApiClient.ConnectionCallbacks,
                                              GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private long interval = LocationSettings.FAST_INTERVAL;

    private long fastestInterval = LocationSettings.FAST_INTERVAL;

    private int locationPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private static GoogleLocationService instance = null;

    Handler h = new Handler();

    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient googleApiClient;

    /**
     * Represents a geographical location.
     */
    private Location currentLocation;

    /**
     * Represents current location state
     */
    private LocationEnum currentLocationState;

    /**
     * Speed vehicle is moving
     */
    private float currentSpeed = 0F;
    private float maxSpeed = 0F;
    public double longitude;
    public double latitude;


    /**
     * Defines the quality of service of gathering location data
     */
    private LocationRequest locationRequest;


    private Context context;

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
            googleApiClient.disconnect();
        }
        h.postDelayed (new Runnable(){

            public void run(){
                googleApiClient.connect();
            }

        }, 2000);

    }




    /**
     * This should be called on applicaion start event
     */
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
    public void onStopActions(){
        if ( googleApiClient.isConnected()){
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
     * updates speed, and calculates the current location state based on the given speed.
     * @param location current location
     */
    private void updateSpeedAndLocationState(Location location) {
        currentSpeed = location.getSpeed();
        if ( currentSpeed <= LocationSettings.IN_VEHICLE_THRESHHOLD ) {
            currentLocationState = LocationEnum.STATIONARY;
        }
        if ( currentSpeed > LocationSettings.IN_VEHICLE_THRESHHOLD ) {
            currentLocationState = LocationEnum.IN_VEHICLE;
        }
        if ( currentSpeed > maxSpeed ) {
            maxSpeed = currentSpeed;
        }
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
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
//        LogService.log( "LOCATION: " + location.getLatitude() );
//        LogService.log( "LOCATION: " + location.getLongitude() );
//        LogService.log( "SPEED: " + location.getSpeed() );
        LogService.log( "Location changed" );

        currentLocation = location;
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

    public Location getCurrentLocation()    {
        return currentLocation;
    }

    public double getLatitude()    {
        if ( currentLocation != null) {
            return currentLocation.getLatitude();
        }
        return 0.0;
    }

    public double getLongitude()    {
        if ( currentLocation != null){
            return currentLocation.getLongitude();
        }
        return 0.0;
    }

    public double getSpeed() {
        if ( currentLocation != null) {
            return currentLocation.getSpeed();
        }
        return 0.0;
    }

    public LocationEnum getLocationState(){return currentLocationState;}
}
