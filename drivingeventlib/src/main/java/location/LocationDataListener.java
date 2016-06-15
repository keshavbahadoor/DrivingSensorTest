package location;

/**
 * Defines an interface that listens for location data change
 *
 * Created by Keshav on 6/6/2016.
 */
public interface LocationDataListener {

    public void handleLocationDataChange(double latitude, double longitude, float speed);
}
