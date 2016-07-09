package fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.driving.senor.test.CustomApplication;
import com.driving.senor.test.R;
import com.google.android.gms.location.LocationRequest;

import keshav.com.drivingeventlib.GLOBALS;
import keshav.com.utilitylib.LogService;
import location.AndroidLocationService;
import location.CustomLocationListener;
import location.GoogleLocationService;
import location.LocationEnum;
import location.LocationSettings;
import location.LocationUpdateDistance;
import location.LocationUpdateTime;

/**
 * Created by Keshav on 1/23/2016.
 */
public class LocationFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private CustomLocationListener customLocationListener;
    private View fragment;
    private Context context;
    TextView location, onFoot, inVehicle;

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            setLocationResults();
        }
    };


    @Override
    public void onCreate(  Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // inflate view
        fragment = inflater.inflate( R.layout.fragment_gps, container, false );
        context = fragment.getContext();

        fragment.findViewById( R.id.btn_getLocation ).setOnClickListener( this );
        fragment.findViewById( R.id.button_location_low ).setOnClickListener( this );
        fragment.findViewById( R.id.button_location_mid ).setOnClickListener( this );
        fragment.findViewById( R.id.button_location_high ).setOnClickListener( this );

        onFoot = (TextView) fragment.findViewById( R.id.tv_onfoot );
        inVehicle = (TextView) fragment.findViewById( R.id.tv_invehicle );
        location = (TextView) fragment.findViewById( R.id.tv_locationdata );

        context.registerReceiver( locationReceiver, new IntentFilter( LocationSettings.BROADCAST_ACTION ) );

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        // This is needed here to avoid the error:
        // Attempt to invoke virtual method 'java.lang.Object android.content.Context.getSystemService(java.lang.String)' on a null object reference
        //customLocationListener = CustomLocationListener.getInstance( context );
    }


    @Override
    public void onClick( View v ) {
        if (v.getId() == R.id.btn_getLocation ) {
            //Log.d( "LOCATION", "Latitude : " + customLocationListener.getLatitude() + " Longitude : " + customLocationListener.getLongitude() );
            //setLocationResults();

            GLOBALS.locationService.switchDebugMode();
            LogService.log("Debug mode on for location: " + GLOBALS.locationService.getLocationState());
        }
        if (v.getId() == R.id.button_location_low) {

            GLOBALS.locationService.setLowAccuracy();
            Toast.makeText( context, "Low accuracy", Toast.LENGTH_SHORT ).show();
        }
        if (v.getId() == R.id.button_location_mid) {

            GLOBALS.locationService.setMidAccuracy();
            Toast.makeText( context, "mid accuracy", Toast.LENGTH_SHORT ).show();
        }
        if (v.getId() == R.id.button_location_high) {

            GLOBALS.locationService.setHighAccuracy();
            Toast.makeText( context, "high accuracy", Toast.LENGTH_SHORT ).show();
        }
    }

    private void setLocationResults()
    {
        location.setText( "" );
        location.append( "LAT : " + customLocationListener.getLatitude() + "\n" );
        location.append( "LONG : " + customLocationListener.getLongitude() + "\n" );
        location.append( "Speed: " + String.format( "%.4f", ( customLocationListener.speed * 3.6 ) ) + "\n" );
        location.append( "Max Speed: " + String.format( "%.4f", ( customLocationListener.maxSpeed * 3.6 ) ) + "\n" );

        if ( customLocationListener.locationState == LocationEnum.IN_VEHICLE ) {
            inVehicle.setVisibility( View.VISIBLE );
            onFoot.setVisibility( View.GONE );
        }
        else {
            inVehicle.setVisibility( View.GONE );
            onFoot.setVisibility( View.VISIBLE );
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        context.stopService( new Intent( context, CustomLocationListener.class ) );
        try {
            context.unregisterReceiver( locationReceiver );
        }
        catch ( Exception ex ) {
            LogService.log( "Receiver not registered" );
        }
    }

}
