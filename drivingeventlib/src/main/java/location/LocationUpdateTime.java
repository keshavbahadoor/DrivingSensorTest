package location;

/**
 * Represents the minimum time in between location updates.
 * Values are in miliseconds
 *
 * Created by Keshav on 6/2/2016.
 */
public class LocationUpdateTime {

    // 0 seconds
    public static final long AGGRESSIVE = 0;

    // 1 minute
    public static final long NORMAL = 1000 * 60;

    // 10 minutes
    public static final long AT_REST = 1000 * 60 * 10;
}
