package location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import keshav.com.drivingeventlib.R;

/**
 * Created by Keshav on 6/1/2016.
 */
public class GoogleLocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private Context context;

    /**
     * Constructor:
     *  - Sets up google api client
     */
    public GoogleLocationService(Context context) {
        this.context = context;
        this.buildGoogleApiClient();
    }

    /**
     * Initializes the google api client object.
     */
    public synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder( this.context )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
    }

    /**
     * This should be called on applicaion start event
     */
    public void onStartActions() {
        mGoogleApiClient.connect();
    }

    /**
     * This should be called on application stop event
     */
    public void onStopActions(){
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected( @Nullable Bundle bundle ) {

        // New requirements for Android M
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {

        } else {
            Toast.makeText(context, "No location detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended( int i ) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed( @NonNull ConnectionResult connectionResult ) {

    }
}
