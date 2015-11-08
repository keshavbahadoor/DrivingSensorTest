package keshav.com.restservice;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Keshav on 10/31/2015.
 */
@Deprecated
public class RestTask extends AsyncTask<String, String, String> {

    public static final int HTTP = 1;
    public static final int HTTPS = 2;
    public static final String BROADCAST_ACTION = "REST_TASK_ACTION";
    private static final String TAG = "RESTTASK";
    private String REST_URL = "";
    private int CONENCTION_TIMEOUT = 1000;
    private int READ_TIMEOUT = 10000;
    private Context context;
    private RequestType requestType;
    private int requestProtocol = HTTP;

    // auth stuff
    private String basicAuthCredentials =   "" ;
    private boolean isBasicAuthCredentialsSet = false;

    // Request data
    private List<String> headerParamKeys;
    private List<String> headerParamValues;
    private ContentValues bodyParams;

    /**
     * Constructor
     * @param context
     */
    public RestTask (Context context ) {
        this.context = context;
        headerParamKeys = new ArrayList<String>();
        headerParamValues = new ArrayList<String>(  );
    }
    public RestTask (Context context, RequestType type) {
        this( context );
        this.requestType = type;
    }

    /**
     * Builds the HTTP request given the supplied data then attempts to send request
     *
     * @param strings
     * @return
     */
    @Override
    protected String doInBackground(String... strings) {

        String response = "";
        URL url ;

        try {
            url = new URL(REST_URL);


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setDefaultHostnameVerifier( new NullHostNameVerifier() );
            //connection.setSSLSocketFactory( SSLSocketFactoryService.createSSLSocketFactoryTrustAll() );

            if ( requestType == RequestType.GET ) {
                connection.setRequestMethod( "GET" );
            } else {
                connection.setRequestMethod( "POST" );
            }

            connection.setConnectTimeout( CONENCTION_TIMEOUT );
            connection.setReadTimeout( READ_TIMEOUT );

            if ( isBasicAuthCredentialsSet ) {
                connection.setRequestProperty("Authorization", basicAuthCredentials);
            }
            // Add supplied request headers
            for ( int i=0; i<headerParamKeys.size(); i++ ) {
                connection.setRequestProperty( headerParamKeys.get( i ), headerParamValues.get( i ) );
            }

            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = RestUtils.readStream(connection.getInputStream());
            }
            else if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                response = RestUtils.readStream(connection.getErrorStream());
            }
            else {
                response = "Response Code: " + responseCode;
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.d(TAG, "Error occured: " + e.getMessage());
            e.printStackTrace();
        }
        Log.d( TAG, response );
        return response;
    }

    @Override
    protected void onPostExecute(String result) {

        Log.d( TAG, "Request fired. Result: " + result );

        // Send broadcast
//        Intent intent = new Intent("intent");
//        intent.setAction(RestService.INTENT_ACTION_NAME);
//        intent.putExtra(RestService.INTENT_ACTION_NAME, result);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//        super.onPostExecute(result);
    }

    /**
     * Adds basic HTTP authorization
     * @param username
     * @param password
     */
    public void addBasicAuth(String username, String password) {
        basicAuthCredentials = "Basic " + Base64.encodeToString( (username + ":" + password).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP );
        isBasicAuthCredentialsSet = true;
    }

    /**
     * Add header params
     */
    public void addHeaderParam(String key, String value) {
        headerParamKeys.add( key );
        headerParamValues.add( value );
    }

    /**
     * Add body params
     */
    public void addBodyParam(String key, String value) {
        bodyParams.put( key, value );
    }
    public void addBodyParam(String key, int value) {
        bodyParams.put(key, value);
    }
    public void addBodyParam(String key, float value) {
        bodyParams.put(key, value);
    }
    public void addBodyParam(String key, double value) {
        bodyParams.put(key, value);
    }
    public void addBodyParam(String key, boolean value) {
        bodyParams.put(key, value);
    }
    public void addBodyParam(String key, long value) {
        bodyParams.put(key, value);
    }


    public void setRequestType( RequestType requestType ) {
        this.requestType = requestType;
    }

    public void setURL(String url) { this.REST_URL = url; }

    public void setReadTimeout( int READ_TIMEOUT ) {
        this.READ_TIMEOUT = READ_TIMEOUT;
    }

    public void setConnectionTimeout( int CONENCTION_TIMEOUT ) {
        this.CONENCTION_TIMEOUT = CONENCTION_TIMEOUT;
    }

    public void setRequestProtocol( int requestProtocol ) {
        this.requestProtocol = requestProtocol;
    }
}
