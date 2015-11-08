package com.driving.senor.test;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import keshav.com.restservice.HTTPRestTask;
import keshav.com.restservice.RequestType;
import keshav.com.restservice.RestTask;
import sensorlib.AccelerometerSensor;
import sensorlib.LocationService;
import sensorlib.SensorService;
import sensorlib.SensorTemplate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationService locationService;
    private static int PERMISSIONS_ACCEPTED;
    List<Sensor> availableSensors;
    TextView text, val1, val2, val3, location;


    /**
     * receives sensor values and sets the text accordingly
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {

            if ( intent.hasExtra( "accelerometer_x" ) ) {
                val1.setText( "X: " + intent.getFloatExtra( "accelerometer_x", 0.0F ) );
                val2.setText( "Y: " + intent.getFloatExtra( "accelerometer_y", 0.0F ) );
                val3.setText( "Z: " + intent.getFloatExtra( "accelerometer_z", 0.0F ) );
            }
            if (intent.getFloatArrayExtra( "gyroscope" ) != null ) {
                ( (TextView) findViewById( R.id.value_gyro_X ) ).setText( "X: " + intent.getFloatArrayExtra( "gyroscope" )[0] );
                ((TextView)  findViewById( R.id.value_gyro_Y )).setText( "Y: " + intent.getFloatArrayExtra( "gyroscope" )[1] );
                ((TextView)  findViewById( R.id.value_gyro_Z )).setText( "Z: " + intent.getFloatArrayExtra( "gyroscope" )[2] );
            }
            if ( intent.getFloatArrayExtra( "rotation" ) != null ) {
                ((TextView)  findViewById( R.id.value_orientation_X )).setText( "X: " + intent.getFloatArrayExtra( "rotation" )[0] );
                ((TextView)  findViewById( R.id.value_orientation_Y )).setText( "Y: " + intent.getFloatArrayExtra( "rotation" )[1] );
                ((TextView)  findViewById( R.id.value_orientation_Z )).setText( "Z: " + intent.getFloatArrayExtra( "rotation" )[2] );
            }
        }
    };
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            setLocationResults();
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // HANDLE PERMISSIONS
        checkApplicationPermissions();

        // set up GUI accessors
        this.findViewById( R.id.fBtn_actionbutton ).setOnClickListener( this );
        this.findViewById( R.id.btn_getLocation ).setOnClickListener( this );
        this.findViewById( R.id.btn_testServer ).setOnClickListener( this );
        //text = (TextView) this.findViewById( R.id.sensor_list );
        val1 = (TextView) this.findViewById( R.id.value_acc_X );
        val2 = (TextView) this.findViewById( R.id.value_acc_Y );
        val3 = (TextView) this.findViewById( R.id.value_acc_Z );
        location = (TextView) this.findViewById( R.id.tv_locationdata );

        // List available sensors
        //availableSensors = ((SensorManager)this.getSystemService( Context.SENSOR_SERVICE )).getSensorList( Sensor.TYPE_ALL );
        //text.setText( "Available Sensors: \n\n" );
        //for (int i=0; i<availableSensors.size(); i++) {
            //text.append( availableSensors.get( i ).getName() + "\n" );
        //}

        // init services
        registerReceiver( receiver, new IntentFilter( SensorService.BROADCAST_ACTION ) );
        registerReceiver( locationReceiver, new IntentFilter( LocationService.BROADCAST_ACTION ) );
    }

    @Override
    protected void onStart() {
        super.onStart();
        // This is needed here to avoid the error:
        // Attempt to invoke virtual method 'java.lang.Object android.content.Context.getSystemService(java.lang.String)' on a null object reference
        locationService = LocationService.getLocationManager( this );
    }

    /**
     * Needed for Android 6.0
     */
    private void checkApplicationPermissions() {

        // List of Permissions we need
//        String[] permissions = {
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//        };
//
//        for (int i=0; i<permissions.length; i++ ) {
//            if (ContextCompat.checkSelfPermission( this, permissions[i] ) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions( this, permissions);
//            }
//        }

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                                                LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
    }

    @Override
    public void onClick( View v ) {

        if (v.getId() == R.id.fBtn_actionbutton) {
            if ( !ServiceUtil.isServiceRunning( SensorService.class, this )) {
                startService( new Intent( this, SensorService.class ) );
            }
            else {
                Snackbar.make(v, "Sensor Service already running", Snackbar.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.btn_getLocation ) {
            Log.d( "LOCATION", "Latitude : " + locationService.getLatitude() + " Longitude : " + locationService.getLongitude() );
            setLocationResults();
        }
        if (v.getId() == R.id.btn_testServer ) {
            HTTPRestTask task = new HTTPRestTask( this.getApplicationContext(), RequestType.POST );
            task.setURL( "http://jsonplaceholder.typicode.com/posts" );
            task.addBodyParam( "name", "keshav" );
            task.execute(  );
        }
    }

    private void setLocationResults()
    {
        location.setText( "" );
        location.append( "LAT : " + locationService.getLatitude() + "\n" );
        location.append( "LONG : " + locationService.getLongitude() + "\n" );
        location.append( "Speed: " + String.format("%.4f", (locationService.speed * 3.6)) + "\n" );
        location.append( "Max Speed: " + String.format("%.4f", (locationService.maxSpeed * 3.6)) + "\n" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService( new Intent( this, SensorService.class ) );
        stopService( new Intent( this, LocationService.class ) );
        unregisterReceiver( receiver );
        unregisterReceiver( locationReceiver );
    }


}
