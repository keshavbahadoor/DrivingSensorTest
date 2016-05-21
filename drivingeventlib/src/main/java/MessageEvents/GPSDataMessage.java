package MessageEvents;

/**
 * Created by Keshav on 2/20/2016.
 */
public class GPSDataMessage  {

    public double latitude;
    public double longitude;
    public float speed;

    public GPSDataMessage() {
        latitude = 0.0;
        longitude = 0.0;
        speed = 0F;
    }

    public void updateData(double latitude, double longitude, float speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
    }
}
