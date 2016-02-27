package keshav.com.drivingeventlib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Required for naive implementation of a data syncing solution
 * TODO Proper solution is to use SyncAdapter
 *
 * Created by Keshav on 2/24/2016.
 */
public class SyncScheduler extends BroadcastReceiver {

    public static final int REQUEST_CODE = 0;
    public static final int FLAGS = 0;
    public static final long SCHEDULE_INTERVAL = 1000 * 60 * 10; // miliseconds x second x minute

    /**
     * Handle the operation when received.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive( Context context, Intent intent ) {

        PowerManager powerManager = (PowerManager) context.getSystemService( Context.POWER_SERVICE );
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "" );
        wakeLock.acquire();
        {

        }
        wakeLock.release();
    }

    /**
     * Sets the schedule for the operation
     * @param context
     */
    public void setSchedule(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        Intent intent = new Intent(context, SyncScheduler.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context, REQUEST_CODE, intent, FLAGS );
        alarmManager.setRepeating( AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), SCHEDULE_INTERVAL, pendingIntent );
    }

    /**
     * Cancels the defined schedule
     * @param context
     */
    public void cancelSchedule(Context context) {

        Intent intent = new Intent(context, SyncScheduler.class);
        PendingIntent sender = PendingIntent.getBroadcast( context, REQUEST_CODE, intent, FLAGS );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        alarmManager.cancel( sender );
    }
}
