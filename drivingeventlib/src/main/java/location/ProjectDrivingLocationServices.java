package location;

/**
 * Created by Keshav on 6/25/2016.
 */
public interface ProjectDrivingLocationServices {

    public void setLowAccuracy();
    public void setMidAccuracy();
    public void setHighAccuracy();
    public void onStartActions();
    public void onStopActions();
    public void onDestroyActions();
    public double getLatitude();
    public double getLongitude();
    public double getSpeed();
    public LocationEnum getLocationState();
}
