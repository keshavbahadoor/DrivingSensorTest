package fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.driving.senor.test.R;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import datalayer.LocalStorage;
import keshav.com.drivingeventlib.ServerRequests;
import datalayer.StoredPrefsHandler;
import keshav.com.utilitylib.DateUtil;
import keshav.com.utilitylib.LogService;
import services.ActivityRecognitionService;
import weather.WeatherData;
import weather.WeatherDataUtil;

/**
 * Created by Keshav on 1/23/2016.
 */
public class LogFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private TextView logWindow;
    private View fragment;
    private Context context;

    /**
     * This is called when the detected activity is changed
     * Origin: ActivityRecognitionService
     */
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            LogService.log( "Activity Recognition Receiver called : GUI" );
            if (logWindow != null) {
                logWindow.append( "Activity: " +
                        ActivityRecognitionService.getNameFromType(
                                intent.getIntExtra( "activity", DetectedActivity.STILL ) ) + "\n");
            }
        }
    };


    @Override
    public void onCreate(  Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        LogService.log( "Log Fragment Created ==" );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // inflate view
        fragment = inflater.inflate( R.layout.fragment_log, container, false );
        context = fragment.getContext();

        logWindow = (TextView) fragment.findViewById( R.id.tv_logs );
        LogService.setLogWindow( logWindow );

        logWindow.append( "Sensor row count: " + LocalStorage.getInstance( context ).getSensorDataCount() );
        logWindow.append( "\nGPS Data row count: " + LocalStorage.getInstance( context ).getGPSDataCount() );

        //ServerRequests.getCurrentWeatherDataEvent( this.getContext(), "10.525820", "-61.409995" ) ;

        LocalBroadcastManager.getInstance( context )
                .registerReceiver( activityReceiver,
                        new IntentFilter( ActivityRecognitionService.BROADCAST_ACTION ) );

        return fragment;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateUI(String str) {
        WeatherData data = WeatherDataUtil.parseJson( str );
        StoredPrefsHandler.storeWeatherData( this.getContext(), data );
        data = StoredPrefsHandler.retrieveWeatherData( this.getContext() );
        logWindow.append( "\nWeather Data:\n\n" + data.toString());
        logWindow.append( "\nDate passed? : " + DateUtil.dateHasPassed( "2016-02-29 11:10:25 PM", +1 ) );
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered( this ))
            EventBus.getDefault().register( this );
    }

    @Override
    public void onClick( View v ) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }
}
