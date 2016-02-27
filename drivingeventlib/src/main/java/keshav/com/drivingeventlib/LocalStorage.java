package keshav.com.drivingeventlib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Keshav on 2/9/2016.
 */
public class LocalStorage extends SQLiteOpenHelper {

    private static LocalStorage instance = null;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "LocalDrivingData";
    private static final String COL_ID = "ID";
    private static final String COL_TIME = "time";
    private static final String TABLE_SensorData = "SensorData";
    private static final String SensorData_AccX = "AccX";
    private static final String SensorData_AccY = "AccY";
    private static final String SensorData_AccZ = "AccZ";
    private static final String TABLE_GPSData = "GPSData";
    private static final String GPSData_latitude = "latitude";
    private static final String GPSData_longitude = "longitude";
    private static final String GPSData_speed = "speed";
    private static final String TABLE_DrivingEvent = "DrivingEvent";
    private SimpleDateFormat dateFormat;
    private Date date;

    // SQL Statements
    private static final String CREATE_TABLE_SensorData = "CREATE TABLE IF NOT EXISTS " + TABLE_SensorData + " (" +
                                                            COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            SensorData_AccX + " TEXT, " + SensorData_AccY + " TEXT, "+SensorData_AccZ+" TEXT, " +
                                                            COL_TIME + " TEXT)" ;
    private static final String CREATE_TABLE_GPSData = "CREATE TABLE IF NOT EXISTS " + TABLE_GPSData + " (" +
                                                            COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            GPSData_latitude + " TEXT, "+GPSData_longitude+" TEXT, "+GPSData_speed+" TEXT, "+COL_TIME+" TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String SELECT_COUNT = "SELECT COUNT(*) FROM ";



    /**
     * Constructor
     * @param context
     */
    protected LocalStorage( Context context  ) {
        super( context, DB_NAME, null, DB_VERSION );
        dateFormat = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
        date = new Date();
    }

    /**
     * Singleton pattern implementation
     * @param context
     * @return
     */
    public static LocalStorage getInstance(Context context) {
        if (instance == null) {
            instance = new LocalStorage( context );
        }
        return instance;
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_TABLE_SensorData );
        db.execSQL( CREATE_TABLE_GPSData );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        if ( newVersion > oldVersion) {
            db.execSQL( DROP_TABLE  + TABLE_GPSData);
            db.execSQL( DROP_TABLE  + TABLE_SensorData);
            this.onCreate( db );
        }
    }

    /**
     * Adds sensor data entry
     * handles the timestamp
     * @param accX
     * @param accY
     * @param accZ
     *
     * TODO : should use internet date for reliability
     */
    public void addSensorData(float accX, float accY, float accZ) {

        ContentValues values = new ContentValues(  );
        values.put( SensorData_AccX, accX );
        values.put( SensorData_AccY, accY );
        values.put( SensorData_AccZ, accZ );
        values.put( COL_TIME, dateFormat.format( date.getTime() ) );
        getReadableDatabase().insert( TABLE_SensorData, null, values );
    }

    /**
     * Adds GPS data entry
     * handles the timestamp
     * @param latitude
     * @param longitude
     * @param speed
     */
    public void addGPSData(String latitude, String longitude, float speed) {

        ContentValues values = new ContentValues(  );
        values.put( GPSData_latitude, latitude );
        values.put( GPSData_longitude, longitude );
        values.put( GPSData_speed, speed );
        values.put( COL_TIME, dateFormat.format( date.getTime() ) );
        getReadableDatabase().insert( TABLE_SensorData, null, values );
    }

    /**
     * Generic function to get the table row count
     * @param table
     * @return
     */
    public String getDatabaseCount(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( SELECT_COUNT + table, null );
        cursor.moveToFirst();
        return cursor.getString( 0 );
    }

    /**
     * Returns amount of rows in sensor data
     * @return
     */
    public String getSensorDataCount() {
        return getDatabaseCount( TABLE_SensorData );
    }

    /**
     * Returns amount of rows in GPS Data sensor
     * @return
     */
    public String getGPSDataCount() {
        return getDatabaseCount( TABLE_GPSData );
    }
}
