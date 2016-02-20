package keshav.com.drivingeventlib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Keshav on 2/10/2016.
 */
public class DrivingPatternService  extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        super.onStartCommand( intent, flags, startId );

        return START_STICKY;
    }




    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}
