package datasync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Keshav on 2/22/2016.
 */
public class Authenticator extends AbstractAccountAuthenticator {

    public Authenticator( Context context ) {
        super( context );
    }

    @Override
    public Bundle editProperties( AccountAuthenticatorResponse response, String accountType ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Don't add any additional accounts
     * @throws NetworkErrorException
     */
    @Override
    public Bundle addAccount( AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options ) throws NetworkErrorException {
        return null;
    }

    /**
     * Ignore attempts to confirm credentials
     * @throws NetworkErrorException
     */
    @Override
    public Bundle confirmCredentials( AccountAuthenticatorResponse response, Account account, Bundle options ) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken( AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options ) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel( String authTokenType ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials( AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options ) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures( AccountAuthenticatorResponse response, Account account, String[] features ) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
