package keshav.com.restservice;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Keshav on 11/7/2015.
 */
public abstract class AbstractRestTask extends AsyncTask<String, String, String>  {

    public static final int HTTP = 1;
    public static final int HTTPS = 2;
    public static final String BROADCAST_ACTION = "REST_TASK_ACTION";
    protected static final String TAG = "RESTTASK";
    protected String REST_URL = "";
    protected int CONENCTION_TIMEOUT = 1000;
    protected int READ_TIMEOUT = 10000;
    protected Context context;
    protected RequestType requestType;
    protected int requestProtocol = HTTP;

    // auth stuff
    protected String basicAuthCredentials =   "" ;
    protected boolean isBasicAuthCredentialsSet = false;

    // Request data
    protected List<String> headerParamKeys;
    protected List<String> headerParamValues;
    protected ContentValues bodyParams;

    /**
     * Constructor
     * @param context
     */
    public AbstractRestTask (Context context ) {
        this.context = context;
        headerParamKeys = new ArrayList<String>();
        headerParamValues = new ArrayList<String>();
        bodyParams = new ContentValues(  );
    }
    public AbstractRestTask (Context context, RequestType type) {
        this( context );
        this.requestType = type;
    }


    /**
     * Adds basic HTTP authorization
     * @param username
     * @param password
     */
    public void addBasicAuth(String username, String password) {
        basicAuthCredentials = "Basic " + Base64.encodeToString( ( username + ":" + password ).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP );
        isBasicAuthCredentialsSet = true;
    }

    /**
     * Creates and returns parameters in string format
     * TODO : Test with GET
     * @return
     * @throws UnsupportedEncodingException
     */
    protected String buildRequestDataPayload() throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder(  );
        boolean first = true;
        for ( Map.Entry<String, Object> entry : bodyParams.valueSet() ) {
            if (first)
                first = false;
            else result.append("&");
            result.append( URLEncoder.encode( entry.getKey(), "UTF-8" ) );
            result.append("=");
            result.append( URLEncoder.encode( entry.getValue().toString(), "UTF-8" ));
        }
        return result.toString();
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

    /**
     * GETTERS AND SETTERS
     */
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
