package keshav.com.restservice;

import android.content.Context;


/**
 * Created by Keshav on 2/28/2016.
 */
public class HTTPRestTaskEvent extends HTTPRestTask {

    public HTTPRestTaskEvent( Context context ) {
        super( context );
        //EventBus.getDefault().register( this );
    }

    public HTTPRestTaskEvent( Context context, RequestType type ) {
        super( context, type );
    }

//    @Subscribe
//    public void handleEvent(Object event) {
//
//    }


    @Override
    protected void onPostExecute( String result ) {

//        EventBus.getDefault().post( result );
//        EventBus.getDefault().unregister( this );
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
//        EventBus.getDefault().unregister( this );
    }
}
