package datasync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Keshav on 2/22/2016.
 */
public class DataSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = "DATA_SYNC_ADAPTER";

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public DataSyncAdapter( Context context, boolean autoInitialize ) {
        super( context, autoInitialize );
        Log.d( TAG, "DataSyncAdapter constructor called." );
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public DataSyncAdapter( Context context, boolean autoInitialize, boolean allowParallelSyncs, ContentResolver mContentResolver ) {
        super( context, autoInitialize, allowParallelSyncs );

        Log.d( TAG, "DataSyncAdapter constructor called." );
        this.mContentResolver = context.getContentResolver();
    }

    /**
     * Performs the data sync
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

        Log.d(TAG, "Beginning network synchronization.");
        // process data for upload

        // upload data via rest calls

        // delete uploaded local data

        Log.d(TAG, "Network synchronization complete.");
    }
}
