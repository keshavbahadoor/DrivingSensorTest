package com.driving.senor.test;

import android.content.Context;

import keshav.com.restservice.HTTPRestTask;
import keshav.com.restservice.RequestType;

/**
 * This is a static class that is just used as an intermediary for the server api
 * uses the simple rest client
 * Created by Keshav on 2/21/2016. *
 */
public class ServerAPI {

    public static final String REGISTER_USER = "registeruser";

    /**
     * Calls the register user api and passes google credentials
     * @param context
     * @param id
     * @param displayName
     * @param email
     * @param photoURL
     * @return
     */
    public static boolean GoogleSignInRequest(Context context, String id, String displayName, String email, String photoURL){

        HTTPRestTask task = new HTTPRestTask( context, RequestType.POST );
        task.setURL( context.getString( R.string.SERVER ) + REGISTER_USER );
        task.addHeaderParam( "X-API-KEY", context.getString( R.string.ApiKey ) );
        task.addBodyParam( "userid", id );
        task.addBodyParam( "displayname", displayName );
        task.addBodyParam( "email", email );
        task.addBodyParam( "photourl", photoURL );
        task.addBodyParam( "usertype", "google" );
        task.execute(  );

        return true;
    }
}
