package location;

/**
 * Created by Keshav on 6/25/2016.
 */
public interface ProjectDrivingLocationServices {

    public void changeAccuracy(long updateTime, long updateDistance);
    public void changeAccuracy(long interval, long fastestInterval, int priority);
    public void onStartActions();
    public void onStopActions();
    public void onDestroyActions();
    public double getLatitude();
    public double getLongitude();
    public double getSpeed();
    public LocationEnum getLocationState();
}
