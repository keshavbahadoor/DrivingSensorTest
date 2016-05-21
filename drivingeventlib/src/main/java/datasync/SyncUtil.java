package datasync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by Keshav on 2/22/2016.
 */
public class SyncUtil {

    public static final String TAG = "SYNC_UTIL_DEBUG";

    // 1 hour (in seconds)
    private static final long SYNC_FREQUENCY = 10;
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "keshav.easy.data.sync.lib";

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "example.com";
    // The account name
    public static final String ACCOUNT_NAME = "dummyaccount";

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {


        // Create the account type and default account
        Account newAccount = new Account( ACCOUNT_NAME, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(context.ACCOUNT_SERVICE);

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            // Inform the system that this account supports sync
            //ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            //ContentResolver.setSyncAutomatically( newAccount, AUTHORITY, true );

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            //ContentResolver.addPeriodicSync( newAccount, AUTHORITY, new Bundle(), SYNC_FREQUENCY);

            Log.d( TAG, "Account created: " + newAccount.name );

            return newAccount;
        } else {
            /*
             * The account exists or some other error occurred.
             * Try and get account first. Then log or report the error and return null.
             */
            Account[] accounts = accountManager.getAccounts();
            for (Account account : accounts) {
                if (account.name.equals( ACCOUNT_NAME )) {
                    Log.d( TAG, "Account exists: " + account.name );
                    return account;
                }
            }
            Log.d(TAG, "Error occured. The account is null");
            return null;
        }

    }
}
