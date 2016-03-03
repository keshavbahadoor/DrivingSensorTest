package keshav.com.restservice;

/**
 * Designed to be used as a callback mechanism for AsyncTask, which is used here for the RestTask
 * services.
 * Created by Keshav on 2/28/2016.
 */
public interface OnTaskComplete {

    /**
     * Handles any related tasks after the current task is completed.
     */
    void onTaskCompleted(String result);
}
