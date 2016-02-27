package MessageEvents;

/**
 * Created by Keshav on 2/20/2016.
 */
public class GPSDataMessage  {

    public String latitude;
    public String longitude;
    public float speed;

    public GPSDataMessage() {
        latitude = "";
        longitude = "";
        speed = 0F;
    }

    public void updateData(String latitude, String longitude, float speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
    }
}
