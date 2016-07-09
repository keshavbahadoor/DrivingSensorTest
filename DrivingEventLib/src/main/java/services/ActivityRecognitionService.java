package services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by Keshav on 7/8/2016.
 */
public class ActivityRecognitionService extends IntentService {

    public static final String BROADCAST_ACTION = "ActivityRecognitionBroadcast";

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    public ActivityRecognitionService( String name ) {
        super( name );
    }

    @Override
    protected void onHandleIntent( Intent intent ) {

        if( ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            int confidence = mostProbableActivity.getConfidence();
            int activityType = mostProbableActivity.getType();

            if (confidence > 65){
//                if (activityType == DetectedActivity.ON_FOOT) {
//                    DetectedActivity betterActivity = walkingOrRunning( result.getProbableActivities() );
//                    if (betterActivity != null) {
//                        currentDetectedActivity = getNameFromType( betterActivity.getType() );
//                    }
//                }

                // notify
                sendNotification( activityType, confidence );
            }
        }
    }

    /**
     * Sends the detected activity via a local broadcast
     * @param detectedActivity
     */
    private void sendNotification(int detectedActivity, int confidence) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra( "activity", detectedActivity );
        intent.putExtra( "confidence", confidence );
        LocalBroadcastManager.getInstance( getApplicationContext() ).sendBroadcast( intent );
        Log.d("ACTIVITY_RECOG", "Broadcast sent");
    }

    /**
     * Further identifies if an ON_FOOT activity is either walking or running.
     * @param probableActivities
     * @return
     */
    private DetectedActivity walkingOrRunning(List<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        int confidence = 0;
        for (DetectedActivity activity : probableActivities) {
            if (activity.getType() != DetectedActivity.RUNNING && activity.getType() != DetectedActivity.WALKING)
                continue;

            if (activity.getConfidence() > confidence)
                myActivity = activity;
        }

        return myActivity;
    }


    /**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    public static String getNameFromType(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.WALKING:
                return "walking";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            default:
                return "unknown";
        }
    }
}
