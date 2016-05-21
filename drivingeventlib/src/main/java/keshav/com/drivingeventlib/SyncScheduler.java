package keshav.com.drivingeventlib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import keshav.com.restservice.OnTaskComplete;

/**
 * Required for naive implementation of a data syncing solution
 * TODO Proper solution is to use SyncAdapter
 *
 * Created by Keshav on 2/24/2016.
 */
public class SyncScheduler extends BroadcastReceiver implements OnTaskComplete {

    public static final int REQUEST_CODE = 0;
    public static final String TAG = "SYNC_SCHEDULER_DEBUG";
    public static final long SCHEDULE_INTERVAL = 2000; // miliseconds x second x minute

    /**
     * Handle the operation when received.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive( Context context, Intent intent ) {

        Log.d( TAG, "Scheduler received!" );


        PowerManager powerManager = (PowerManager) context.getSystemService( Context.POWER_SERVICE );
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "" );
        wakeLock.acquire();
        {
            Toast.makeText( context, "Alarm activated", Toast.LENGTH_LONG ).show();

            ServerRequests.getCurrentWeatherData ( context, this, "10.525820", "-61.409995") ;
            // update weather data
        }
        wakeLock.release();
    }

    /**
     * Sets the schedule for the operation
     * @param context
     */
    public void setSchedule(Context context) {

        Log.d( TAG, "Alarm set!" );
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.SECOND, 30 );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        Intent intent = new Intent(context, SyncScheduler.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        alarmManager.setRepeating( AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent );

    }

    /**
     * Cancels the defined schedule
     * @param context
     */
    public void cancelSchedule(Context context) {

        Intent intent = new Intent(context, SyncScheduler.class);
        PendingIntent sender = PendingIntent.getBroadcast( context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        alarmManager.cancel( sender );
    }

    @Override
    public void onTaskCompleted( String result ) {
        Log.d( TAG, "Task completed!! " + result );
    }
}
