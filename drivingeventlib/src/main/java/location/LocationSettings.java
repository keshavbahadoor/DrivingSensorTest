package location;

/**
 * Predefined location settings
 * Created by Keshav on 6/6/2016.
 */
public class LocationSettings {

    public static final long FAST_INTERVAL = 0L;

    // 1 minute
    public static final long MEDIUM_INTERVAL = 20000L;

    // 10 minutes
    public static final long SLOW_INTERVAL = 600000L;

    /**
     * in ms^-1 (approx. 20 kmp/h)
     */
    public static final float IN_VEHICLE_THRESHOLD = 5.5555F;

    public static final String BROADCAST_ACTION = "LOCATION_CHANGED_BROADCAST ";

}
