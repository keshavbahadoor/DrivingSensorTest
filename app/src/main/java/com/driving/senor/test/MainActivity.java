package com.driving.senor.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import sensorlib.LocationEnum;
import sensorlib.LocationService;
import sensorlib.SensorService;
import util.LogService;
import util.ServiceUtil;

public class MainActivity extends AppCompatActivity  {

    private ViewPager viewPager;
    private TabsPageAdapter adapter;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.main_view_pager );

        // HANDLE PERMISSIONS
        checkApplicationPermissions();

        viewPager = (ViewPager) this.findViewById( R.id.pager );
        adapter = new TabsPageAdapter( this.getSupportFragmentManager(), this );
        viewPager.setAdapter( adapter );

        LogService.log( "Started" );

    }


    /**
     * Needed for Android 6.0
     */
    private void checkApplicationPermissions() {
 
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopService( new Intent( this, SensorService.class ) );
        stopService( new Intent( this, LocationService.class ) );
    }

}
