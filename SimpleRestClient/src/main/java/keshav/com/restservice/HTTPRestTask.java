package keshav.com.restservice;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Keshav on 11/7/2015.
 */
public class HTTPRestTask extends AbstractRestTask {



    public HTTPRestTask( Context context ) {
        super( context );
    }
    public HTTPRestTask( Context context, RequestType type ) {
        super( context, type );
    }
    public HTTPRestTask( Context context, RequestType type, OnTaskComplete listener) { super (context, type, listener); }

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

            if ( requestType == RequestType.GET ) {
                connection.setRequestMethod( "GET" );
            } else {
                connection.setRequestMethod( "POST" );
            }
            connection.setDoInput( true );
            connection.setConnectTimeout( CONENCTION_TIMEOUT );
            connection.setReadTimeout( READ_TIMEOUT );

            if ( isBasicAuthCredentialsSet ) {
                connection.setRequestProperty("Authorization", basicAuthCredentials);
            }
            // Add supplied request headers
            for ( int i=0; i<headerParamKeys.size(); i++ ) {
                connection.setRequestProperty( headerParamKeys.get( i ), headerParamValues.get( i ) );
            }

            // Add body parameters
            if ( bodyParams.size() > 0 ) {
                connection.setDoOutput( true );
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );
                writer.write( buildRequestDataPayload() );
                writer.flush();
                writer.close();
                os.close();
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            Log.d( TAG, "Response code: " + responseCode );
            if (responseCode == HttpURLConnection.HTTP_OK ||
                responseCode == HttpURLConnection.HTTP_CREATED) {
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
        currentResponse = response;
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute( result );
        Log.d( TAG, "Request fired. Result: " + result );
    }



}
