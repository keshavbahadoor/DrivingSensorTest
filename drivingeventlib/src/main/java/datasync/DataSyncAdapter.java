package datasync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;

import api.Response;
import api.ServerAPI;
import datalayer.LocalStorage;
import datalayer.StoredPrefsHandler;
import keshav.com.drivingeventlib.GLOBALS;
import keshav.com.drivingeventlib.RetrofitServiceGenerator;
import keshav.com.restservice.OnTaskComplete;
import keshav.com.utilitylib.LogService;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Keshav on 2/22/2016.
 */
public class DataSyncAdapter extends AbstractThreadedSyncAdapter implements OnTaskComplete {

    public static final String TAG = "DATA_SYNC_ADAPTER";
    public static final int MAX_ROWS_TO_SYNC = 300;

    private Context context;
    private String googleId;

    private JSONArray sensorDataArray;
    private JSONArray gpsDataArray;

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public DataSyncAdapter( Context context, boolean autoInitialize ) {
        super( context, autoInitialize );
        this.context = context;
        mContentResolver = context.getContentResolver();
        googleId = StoredPrefsHandler.getGoogleAccountID( context );
        Log.d( TAG, "DataSyncAdapter object created." );
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public DataSyncAdapter( Context context, boolean autoInitialize, boolean allowParallelSyncs, ContentResolver mContentResolver ) {
        super( context, autoInitialize, allowParallelSyncs );
        this.context = context;
        this.mContentResolver = context.getContentResolver();
        googleId = StoredPrefsHandler.getGoogleAccountID( context );
        Log.d( TAG, "DataSyncAdapter object created." );
    }

    /**
     * Callback for retrofit API requests
     */
    private Callback<Response> deleteDataCallback = new Callback<Response>() {
        @Override
        public void onResponse( Call<Response> call, retrofit2.Response<Response> response ) {
            LogService.log( "SUCCESS: " + call.toString()  + " " + response.message() );

        }

        @Override
        public void onFailure( Call<Response> call, Throwable t ) {
            LogService.log( "ERROR: " + call.toString()  );
        }
    };

    /**
     * Performs the data sync
     * Data sync is done as follows:
     * - We select the last n records from the database for both GPS data and sensor data
     *   Data will be returned in JSONArray format.
     * - Convert the JSONArray to a string, which will be the payload for the API call
     * - Send data to the server.
     * - If the server receives data successfully, we delete the data selected from the local storage
     *   using the ID columns
     *
     * @param account
     * An Account object associated with the event that triggered the sync adapter. If your server doesn't use accounts, you don't need to use the information in this object.
     *
     * @param extras
     * A Bundle containing flags sent by the event that triggered the sync adapter.
     *
     * @param authority
     * The authority of a content provider in the system. Your app has to have access to this provider. Usually, the authority corresponds to a content provider in your own app.
     *
     * @param provider
     * A ContentProviderClient for the content provider pointed to by the authority argument. A ContentProviderClient is a lightweight public interface to a content provider.
     * It has the same basic functionality as a ContentResolver. If you're using a content provider to store data for your app, you can connect to the provider with this object.
     * Otherwise, you can ignore it.
     *
     * @param syncResult
     * A SyncResult object that you use to send information to the sync adapter framework.
     */
    @Override
    public void onPerformSync( Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult ) {

        Log.d(TAG, "____ Beginning network synchronization.");
        LocalStorage.getInstance( context ).addLogEntry( "SYNC_ADAPTER", "Start network sync" );
        if ( GLOBALS.DEBUG_DATA_SYNC ) {
            LogService.log( "DEBUG SYNC ON .. RETURNING" );
            return;
        }
        if ( googleId.length() == 0 ) {
            googleId = StoredPrefsHandler.getGoogleAccountID( context );
        }
        try {
            /**
             * Get the required data into instance JSONArray
             */
            gpsDataArray = LocalStorage.getInstance( context )
                    .getDataAsJsonArray( LocalStorage.TABLE_GPSData, MAX_ROWS_TO_SYNC );
            sensorDataArray = LocalStorage.getInstance( context )
                    .getDataAsJsonArray( LocalStorage.TABLE_SensorData, MAX_ROWS_TO_SYNC );

            LogService.log( "   Data to sync: " +gpsDataArray.length()+ ", " + sensorDataArray.length() );
            LocalStorage.getInstance( context ).addLogEntry( "SYNC_ADAPTER", "Data to sync: " +gpsDataArray.length()+ ", " + sensorDataArray.length( ));

            /**
             * Do GPS Posting and clear data on success
             */
            if (gpsDataArray.length() > 0) {
                ServerAPI api = RetrofitServiceGenerator.createService( ServerAPI.class );
                Call<Response> call = api.addGPSDataBulk( googleId, gpsDataArray.toString() );
                call.enqueue( new Callback<Response>() {
                    @Override
                    public void onResponse( Call<Response> call, retrofit2.Response<Response> response ) {
                        if ( response != null
                                && response.isSuccessful()
                                && response.body().getSuccess() != null
                                && response.body().getSuccess().length() > 0 ) {
                            clearLocalGPSData();
                        }
                    }

                    @Override
                    public void onFailure( Call<Response> call, Throwable t ) {
                    }
                } );
            }

            /**
             * Do Sensor posting and clear data on success
             */
            if (sensorDataArray.length() > 0) {
                ServerAPI api = RetrofitServiceGenerator.createService( ServerAPI.class );
                Call<Response> call = api.addAccelerationDataBulk( googleId, sensorDataArray.toString() );
                call.enqueue( new Callback<Response>() {
                    @Override
                    public void onResponse( Call<Response> call, retrofit2.Response<Response> response ) {
                        if ( response != null
                                && response.isSuccessful()
                                && response.body().getSuccess() != null
                                && response.body().getSuccess().length() > 0 ) {
                            clearLocalSensorData();
                        }
                    }

                    @Override
                    public void onFailure( Call<Response> call, Throwable t ) {
                    }
                } );
            }

        } catch ( Exception e ) {
            Log.d(TAG, "Error perforiming data sync!: " + e.getMessage());
        }
        Log.d(TAG, "____ Network synchronization complete.");
        LocalStorage.getInstance( context ).addLogEntry( "SYNC_ADAPTER", "Finish network sync" );
    }

    /**
     * Deletes the local copy of records for the given data and table
     * @param array
     * @param tableName
     */
    private void deleteLocalCopy(JSONArray array, String tableName) {

        LocalStorage.getInstance( context ).addLogEntry( "SYNC_ADAPTER", "Deleting from " + tableName );
        Log.d(TAG, "Deleting from "  + tableName);
        try {
            LocalStorage.getInstance( context ).deleteFromBetween( tableName,
                    array.getJSONObject( 0 ).getString( LocalStorage.COL_ID ),
                    array.getJSONObject( array.length() - 1 ).getString( LocalStorage.COL_ID ));
        } catch ( Exception ex ) {
            Log.d( TAG, "Error deleting rows: " + ex.getMessage() );
        }
    }

    /**
     * Clears the local gps data. Called on retrofit on success
     */
    private void clearLocalGPSData() {
        if (gpsDataArray != null) {
            deleteLocalCopy( gpsDataArray, LocalStorage.TABLE_GPSData );
        }
    }

    /**
     * Clears the local sensor data. Called on retrofit on success
     */
    private void clearLocalSensorData() {
        if (sensorDataArray != null){
            deleteLocalCopy( sensorDataArray, LocalStorage.TABLE_SensorData );
        }
    }

    @Override
    public void onTaskCompleted( String result ) {


    }
}
