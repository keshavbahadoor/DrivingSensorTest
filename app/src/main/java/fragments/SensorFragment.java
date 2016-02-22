package fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import MessageEvents.AccelerationDataMessage;
import keshav.com.drivingeventlib.DrivingPatternService;
import sensor.lib.SensorService;
import com.driving.senor.test.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import keshav.com.utilitylib.LogService;
import keshav.com.utilitylib.ServiceUtil;


/**
 * Created by Keshav on 1/23/2016.
 */
public class SensorFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private View fragment;
    private Context context;
    float [] max_accel, max_gyro;

    /**
     * receives sensor values and sets the text accordingly
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {

            int k;
            float vals[];

            try {
                if ( intent.getFloatArrayExtra( "accelerometer" ) != null ) {
                    vals = intent.getFloatArrayExtra( "accelerometer" );
                    ( (TextView) fragment.findViewById( R.id.value_acc_X ) ).setText( "X: " + vals[0] );
                    ( (TextView) fragment.findViewById( R.id.value_acc_Y ) ).setText( "Y: " + vals[1] );
                    ( (TextView) fragment.findViewById( R.id.value_acc_Z ) ).setText( "Z: " + vals[2] );

                    for ( k = 0; k < 3; k++ ) {
                        if ( vals[k] > max_accel[k] )
                            max_accel[k] = vals[k];
                    }

                    setMaxValues( max_accel, R.id.value_acc_X_max, R.id.value_acc_Y_max, R.id.value_acc_Z_max );
                }
                if ( intent.getFloatArrayExtra( "gyroscope" ) != null ) {
                    vals = intent.getFloatArrayExtra( "gyroscope" );
                    ( (TextView) fragment.findViewById( R.id.value_gyro_X ) ).setText( "X: " + vals[0] );
                    ( (TextView) fragment.findViewById( R.id.value_gyro_Y ) ).setText( "Y: " + vals[1] );
                    ( (TextView) fragment.findViewById( R.id.value_gyro_Z ) ).setText( "Z: " + vals[2] );

                    for ( k = 0; k < 3; k++ ) {
                        if ( vals[k] > max_gyro[k] )
                            max_gyro[k] = vals[k];
                    }

                    setMaxValues( max_gyro, R.id.value_gyro_X_max, R.id.value_gyro_Y_max, R.id.value_gyro_Z_max );
                }
                if ( intent.getFloatArrayExtra( "rotation" ) != null ) {
                    vals = intent.getFloatArrayExtra( "rotation" );
                    ( (TextView) fragment.findViewById( R.id.value_orientation_X ) ).setText( "X: " + vals[0] );
                    ( (TextView) fragment.findViewById( R.id.value_orientation_Y ) ).setText( "Y: " + vals[1] );
                    ( (TextView) fragment.findViewById( R.id.value_orientation_Z ) ).setText( "Z: " + vals[2] );

                }

                if ( intent.getFloatArrayExtra( "gravity" ) != null ) {
                    vals = intent.getFloatArrayExtra( "gravity" );
                    ( (TextView) fragment.findViewById( R.id.value_gravity_x ) ).setText( "X: " + vals[0] );
                    ( (TextView) fragment.findViewById( R.id.value_gravity_Y ) ).setText( "Y: " + vals[1] );
                    ( (TextView) fragment.findViewById( R.id.value_gravity_Z ) ).setText( "Z: " + vals[2] );

                }
            } catch ( Exception e ) {
                LogService.log( "Error occured getting sensor values from broadcast: " + e.getMessage() );
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register( this );
    }

    @Override
    public void onCreate(  Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        max_accel = new float[] {0.0F, 0.0F, 0.0F};
        max_gyro = new float[] {0.0F, 0.0F, 0.0F};
    }

    /**
     * Set up views and init stuff
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // inflate view
        fragment = inflater.inflate( R.layout.fragment_sensor, container,false);
        context = fragment.getContext();

        fragment.findViewById( R.id.fBtn_actionbutton ).setOnClickListener( this );
        fragment.findViewById( R.id.btn_reset ).setOnClickListener( this );

        context.registerReceiver( receiver, new IntentFilter( SensorService.BROADCAST_ACTION ) );
        return fragment;
    }

    /**
     * must kill services and unregister any receivers
     */
    @Override
    public void onStop() {
        super.onStop();
        context.stopService( new Intent( context, SensorService.class ) );
        try {
            context.unregisterReceiver( receiver );
            EventBus.getDefault().unregister( this );
        }
        catch ( Exception ex ) {
            LogService.log( "Receiver not registered" );
        }
    }

    @Override
    public void onClick( View v ) {
        if (v.getId() == R.id.fBtn_actionbutton) {
            if ( !ServiceUtil.isServiceRunning( SensorService.class, context )) {
                // context.startService( new Intent( context, SensorService.class ) );
                context.startService( new Intent(context, DrivingPatternService.class) );
            }
            else {
                Snackbar.make( v, "Sensor Service already running", Snackbar.LENGTH_LONG ).show();
            }
        }
        if (v.getId() == R.id.btn_reset) {
            Log.d( "RESET", "pressed" );
            max_accel = new float[] {0.0F, 0.0F, 0.0F};
            max_gyro = new float[] {0.0F, 0.0F, 0.0F};
            setMaxValues( max_accel, R.id.value_acc_X_max, R.id.value_acc_Y_max, R.id.value_acc_Z_max );
            setMaxValues( max_gyro, R.id.value_gyro_X_max, R.id.value_gyro_Y_max, R.id.value_gyro_Z_max );
        }
    }

    private void setMaxValues(float[] vals, int id1, int id2, int id3)
    {
        ( (TextView) fragment.findViewById( id1 ) ).setText( "X: " + vals[0] );
        ( (TextView) fragment.findViewById( id2 ) ).setText( "Y: " + vals[1] );
        ( (TextView) fragment.findViewById( id3 ) ).setText( "Z: " + vals[2] );
    }

    @Subscribe
    public void onMessageEvent( AccelerationDataMessage message ) {

        LogService.log( "Received a message" );
        ( (TextView) fragment.findViewById( R.id.value_acc_X ) ).setText( "X: " + message.sensorVals[0] );
        ( (TextView) fragment.findViewById( R.id.value_acc_Y ) ).setText( "Y: " + message.sensorVals[1] );
        ( (TextView) fragment.findViewById( R.id.value_acc_Z ) ).setText( "Z: " + message.sensorVals[2] );
    }


}
