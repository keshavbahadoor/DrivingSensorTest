package com.driving.senor.test;

import android.accounts.Account;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import datasync.DataSyncAdapter;
import datasync.SyncUtil;
import location.CustomLocationListener;
import services.DrivingService;
import user.management.GooglePlusHandler;
import keshav.com.utilitylib.DialogFactory;
import keshav.com.utilitylib.LogService;
import user.management.UserData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SyncStatusObserver {

    public static final String CURRENT_ACCOUNT_ID_PREF = "ACCOUNT_ID_NUMBER";
    private GooglePlusHandler googlePlusHandler;
    private Dialog signInDialog;
    private Toolbar toolbar;
    private DataSyncAdapter dataSyncAdapter;
    private Account mAccount;

    private DrivingService drivingService;
    private boolean mBounded;
    private DrivingService.LocalBinder drivingServiceBinder;
//    ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected( ComponentName name, IBinder service ) {
//            LogService.log( "====> Drive Service Connected! " );
//            mBounded = true;
//            drivingServiceBinder = (DrivingService.LocalBinder) service;
//            drivingService =  drivingServiceBinder.getService();
//        }
//
//        @Override
//        public void onServiceDisconnected( ComponentName name ) {
//            LogService.log( "====> Drive Service Disconnected! " );
//            mBounded = false;
//            drivingService = null;
//        }
//    };


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

        signInDialog = DialogFactory.getSignInDialog( this, "Sign In", getLayoutInflater().inflate( R.layout.sign_in_dialog, null ) );
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
                    CustomLocationListener.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (! googlePlusHandler.isSignedIn()) {
            signInDialog.show();
        } else {
            launchServiceAndSyncAdapter();
        }
    }

    /**
     * Launches Driving service as well as the sync adapter.
     * Sync adapter is responsible for transfering data to server.
     */
    private void launchServiceAndSyncAdapter() {
        this.startService( new Intent( this.getApplicationContext(), DrivingService.class ) );

//        Intent intent = new Intent(getApplicationContext(), DrivingService.class);
//        bindService( intent, mConnection, BIND_AUTO_CREATE );
//        mBounded = true;

        ((CustomApplication) getApplication()).setDrivingService( drivingService );

        mAccount = SyncUtil.CreateSyncAccount( this );
        if (mAccount != null) {
            dataSyncAdapter = new DataSyncAdapter( this, true );
            ContentResolver.addStatusChangeListener(1, this);
            ContentResolver.setIsSyncable( mAccount, SyncUtil.AUTHORITY, 1 );
            ContentResolver.setSyncAutomatically( mAccount, SyncUtil.AUTHORITY, true );
            ContentResolver.addPeriodicSync( mAccount, SyncUtil.AUTHORITY, Bundle.EMPTY, 10 );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopService( new Intent( this, SensorService.class ) );
//        stopService( new Intent( this, CustomLocationListener.class ) );
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
     * sends data to server
     * stores data locally
     * starts data cach service
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent intent ) {
        super.onActivityResult( requestCode, resultCode, intent );

        if (requestCode == googlePlusHandler.RC_SIGN_IN) {
            signInDialog.dismiss();
            googlePlusHandler.setSignInResult( intent );

            UserData data = googlePlusHandler.getUserData();
            ServerAPI.GoogleSignInRequest( this, data.googleID, data.displayName, data.email, data.googlePhotoURL );

            SharedPreferences.Editor editor = this.getSharedPreferences( CURRENT_ACCOUNT_ID_PREF, Context.MODE_PRIVATE ).edit();
            editor.putString( "AccountID", data.googleID );
            editor.commit();


        }
    }

    /**
     * Called when the status of the sync adapter observer changes
     * @param which
     */
    @Override
    public void onStatusChanged( int which ) {
        LogService.log( "Status changed: " + which);
        LogService.log(  "is Sync active: " + ContentResolver.isSyncActive( mAccount, SyncUtil.AUTHORITY ));
        LogService.log(  "is Sync Pending: " + ContentResolver.isSyncPending( mAccount, SyncUtil.AUTHORITY ) );
    }
}
