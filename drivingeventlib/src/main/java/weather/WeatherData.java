package weather;

/**
 * Simple version to parse Open Weather Data APIs
 * Created by Keshav on 2/27/2016.
 */
public class WeatherData {

    public String city = "";
    public int rainVolume = 0;

    // weather array data
    public int weatherID = 0;
    public String main = "";
    public String description = "";
    public String weatherIcon = "";

    // Wind data
    public double windSpeed = 0;

    // main
    public double temperature = 0;
    public double pressure = 0;
    public double humidity = 0;
    public double minTemp = 0;
    public double maxTemp = 0;

    // date of capture
    public String date = "";

    /**
     * To string method
     * @return weather info
     */
    public String toString() {
        return "city: " + city +
                "\nWeather ID: " + weatherID +
                "\nrain: " + rainVolume +
                "\nweather main: " + main +
                "\ndescription: " + description +
                "\nweather icon: " + weatherIcon +
                "\nwind speed: " + windSpeed +
                "\ntemperature: " + temperature +
                "\npressure: " + pressure +
                "\nhumidity: " + humidity +
                "\nmin temp: " + minTemp +
                "\nmax temp: " + maxTemp +
                "\ndate: " + date;
     }

}
