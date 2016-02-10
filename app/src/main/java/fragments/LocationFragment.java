package fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.driving.senor.test.R;
import sensor.lib.LocationEnum;
import sensor.lib.LocationService;
import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 1/23/2016.
 */
public class LocationFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private LocationService locationService;
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

        onFoot = (TextView) fragment.findViewById( R.id.tv_onfoot );
        inVehicle = (TextView) fragment.findViewById( R.id.tv_invehicle );
        location = (TextView) fragment.findViewById( R.id.tv_locationdata );

        context.registerReceiver( locationReceiver, new IntentFilter( LocationService.BROADCAST_ACTION ) );

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        // This is needed here to avoid the error:
        // Attempt to invoke virtual method 'java.lang.Object android.content.Context.getSystemService(java.lang.String)' on a null object reference
        locationService = LocationService.getLocationManager( context );
    }


    @Override
    public void onClick( View v ) {
        if (v.getId() == R.id.btn_getLocation ) {
            Log.d( "LOCATION", "Latitude : " + locationService.getLatitude() + " Longitude : " + locationService.getLongitude() );
            setLocationResults();
        }
    }

    private void setLocationResults()
    {
        location.setText( "" );
        location.append( "LAT : " + locationService.getLatitude() + "\n" );
        location.append( "LONG : " + locationService.getLongitude() + "\n" );
        location.append( "Speed: " + String.format( "%.4f", ( locationService.speed * 3.6 ) ) + "\n" );
        location.append( "Max Speed: " + String.format( "%.4f", ( locationService.maxSpeed * 3.6 ) ) + "\n" );

        if (locationService.locationState == LocationEnum.IN_VEHICLE ) {
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
        context.stopService( new Intent( context, LocationService.class ) );
        try {
            context.unregisterReceiver( locationReceiver );
        }
        catch ( Exception ex ) {
            LogService.log( "Receiver not registered" );
        }
    }

}
