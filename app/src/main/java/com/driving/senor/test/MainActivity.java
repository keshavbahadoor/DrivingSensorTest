package com.driving.senor.test;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sensorlib.LocationService;
import sensorlib.SensorService;
import user.management.GooglePlusHandler;
import util.DialogFactory;
import util.LogService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GooglePlusHandler googlePlusHandler;
    private Dialog signInDialog;
    private Toolbar toolbar;



    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.main_view_pager );

        // HANDLE PERMISSIONS
        checkApplicationPermissions();

        final ViewPager viewPager = (ViewPager) this.findViewById( R.id.pager );
        final TabsPageAdapter adapter = new TabsPageAdapter( this.getSupportFragmentManager(), this );
        viewPager.setAdapter( adapter );
        googlePlusHandler = new GooglePlusHandler( this );
        LogService.log( "Started" );

        signInDialog = DialogFactory.getSignInDialog( this, "Sign In" );
        signInDialog.findViewById( R.id.Btn_google_sign_in ).setOnClickListener( this );
        signInDialog.findViewById( R.id.Btn_sign_in_cancel ).setOnClickListener( this );

        toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
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
    protected void onStart() {
        super.onStart();

        if (! googlePlusHandler.isSignedIn()) {
            signInDialog.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService( new Intent( this, SensorService.class ) );
        stopService( new Intent( this, LocationService.class ) );
    }

    @Override
    public void onClick( View v ) {
        if (v.getId() == R.id.Btn_google_sign_in) {
            googlePlusHandler.gSignIn();
        }
        if (v.getId() == R.id.Btn_sign_in_cancel) {
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    public void signOutUser(MenuItem item){
        googlePlusHandler.gSignOut();
        Toast.makeText(this, "You have signed out", Toast.LENGTH_LONG).show();
        this.finish();
        // kill any background services
    }

    /**
     * Handles the Google plus user sign in after result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == googlePlusHandler.RC_SIGN_IN) {
            signInDialog.dismiss();
            googlePlusHandler.setSignInResult( data );
            googlePlusHandler.getUserData();
        }
    }
}
