package user.management;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 2/7/2016.
 */
public class GooglePlusHandler implements GoogleApiClient.OnConnectionFailedListener,
                                          GoogleApiClient.ConnectionCallbacks
{

    private static final String GOOGLE_SIGN_IN_STORE = "Google_Plus_Sign_in";
    private static final String SIGNED_IN_PREFERENCE = "Google_Plus_Sign_in_state";
    public static final int RC_SIGN_IN = 0;
    private Context context;
    private FragmentActivity mainActivity;
    private GoogleApiClient googleApiClient;
    private ProgressDialog progressDialog;
    private Intent signInIntent;
    private Intent signInResultIntent;

    public GooglePlusHandler(FragmentActivity activity) {
        mainActivity = activity;
        context = mainActivity.getApplicationContext();
        progressDialog = new ProgressDialog( mainActivity );
        progressDialog.setMessage( "Signing in..." );
        buildGoogleServices();
    }

    /**
     * Creates google sign in options object, and google api client object.
     * Attempts connection
     */
    public void buildGoogleServices() {

        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                    .requestEmail()
                    .build();
            googleApiClient = new GoogleApiClient.Builder( context )
                    .enableAutoManage( mainActivity, this )
                    .addApi( Auth.GOOGLE_SIGN_IN_API, gso )
                    .addConnectionCallbacks( this )
                    .build();
            googleApiClient.connect();
        }  catch ( Exception ex ) {
            LogService.log( "Error building google api services: " + ex.getMessage() );
        }
    }



    /**
     * returns true if user has signed in already
     * false if otherwise
     * @return
     */
    public boolean isSignedIn() {
        try {
            SharedPreferences prefs = context.getSharedPreferences( GOOGLE_SIGN_IN_STORE, Context.MODE_PRIVATE );
            boolean signedIn = prefs.getBoolean( SIGNED_IN_PREFERENCE, false );
            LogService.log( "User Signed in? : " + signedIn );
            return signedIn;
        } catch ( Exception ex ) {
            LogService.log( "Exception checking prefs: " + ex.getMessage() );
            return false;
        }
    }

    /**
     * sets the user sign in state
     */
    public void setSignedIn(boolean signedInState) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences( GOOGLE_SIGN_IN_STORE, Context.MODE_PRIVATE ).edit();
            editor.putBoolean( SIGNED_IN_PREFERENCE, signedInState );
            editor.commit();
        } catch (Exception ex) {
            // do something
        }
    }

    /**
     * Initiates a sign in request
     */
    public void gSignIn() {
        if (googleApiClient.isConnected()) {
            signInIntent = Auth.GoogleSignInApi.getSignInIntent( googleApiClient );
            mainActivity.startActivityForResult( signInIntent, RC_SIGN_IN );
            progressDialog.show();
        } else {
            LogService.log( "WARNING: google api client is not connected" );
        }
    }

    /**
     * Initiates a sign out request
     */
    public void gSignOut() {
        Auth.GoogleSignInApi.signOut( googleApiClient ).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult( Status status ) {
                        // update any UI stuff here

                    }
                }
        );
        setSignedIn( false );
    }

    /**
     * Gets user data from the currently signed in account
     */
    public void getUserData() {

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent( signInResultIntent );
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();
            LogService.log("name: " + account.getDisplayName());
            LogService.log( "email: "  + account.getEmail() );
            LogService.log( "photo: " + account.getPhotoUrl() );
        } else {
            LogService.log( "Sign in was not successful" );
        }
    }

    /**
     * used to know if the service can be used
     * @return
     */
    public boolean isActive() {
        return googleApiClient.isConnected();
    }


    public void onStart() {
        googleApiClient.connect();
    }

    public void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionFailed( ConnectionResult connectionResult ) {
        LogService.log( "ERROR: Connection failed: " + connectionResult.getErrorMessage() );
    }

    @Override
    public void onConnected( Bundle bundle ) {
        LogService.log("Google APIs connected!");
    }

    @Override
    public void onConnectionSuspended( int i ) {

    }

    /**
     * Sign in result intent is used for getting any account related datea
     * from the Google Signed in account. This is passed back via intent.
     *
     * Progress dialog must be dismissed as well as this call is intented for
     * after the sign in process
     * @param intent
     */
    public void setSignInResult(Intent intent) {
        progressDialog.dismiss();
        signInResultIntent = intent;
    }
}
