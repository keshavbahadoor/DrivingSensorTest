package datalayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import keshav.com.utilitylib.LogService;

/**
 * Created by Keshav on 2/9/2016.
 */
public class LocalStorage extends SQLiteOpenHelper {

    private static LocalStorage instance = null;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "LocalDrivingData";
    public static final String COL_ID = "ID";
    public static final String COL_TIME = "time";
    public static final String TABLE_SensorData = "SensorData";
    private static final String SensorData_AccX = "AccX";
    private static final String SensorData_AccY = "AccY";
    private static final String SensorData_AccZ = "AccZ";
    public static final String TABLE_GPSData = "GPSData";
    private static final String GPSData_latitude = "latitude";
    private static final String GPSData_longitude = "longitude";
    private static final String GPSData_speed = "speed";
    private static final String GPSData_weatherid = "weatherid";
    private static final String GPSData_rain = "rain";
    private static final String GPSData_wind = "wind";
    private static final String GPSData_temp = "temp";
    private static final String GPSData_pressure = "pressure";
    private static final String GPSData_humidity = "humidity";
    //public static final String TABLE_DrivingEvent = "DrivingEvent";
    private SimpleDateFormat dateFormat;
    private Date date;

    // SQL Statements
    private static final String CREATE_TABLE_SensorData = "CREATE TABLE IF NOT EXISTS " + TABLE_SensorData + " (" +
                                                            COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            SensorData_AccX +       " TEXT, " +
                                                            SensorData_AccY +       " TEXT, "+
                                                            SensorData_AccZ+        " TEXT, " +
                                                            COL_TIME + " TEXT)" ;
    private static final String CREATE_TABLE_GPSData = "CREATE TABLE IF NOT EXISTS " + TABLE_GPSData + " (" +
                                                            COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            GPSData_latitude +      " TEXT, "+
                                                            GPSData_longitude+      " TEXT, "+
                                                            GPSData_speed+          " TEXT, "+
                                                            GPSData_weatherid+      " INTEGER, "+
                                                            GPSData_rain+           " DOUBLE, "+
                                                            GPSData_wind+           " DOUBLE, "+
                                                            GPSData_temp+           " DOUBLE, "+
                                                            GPSData_pressure+       " DOUBLE, "+
                                                            GPSData_humidity+       " DOUBLE, "+
                                                            COL_TIME+               " TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String SELECT_COUNT = "SELECT COUNT(*) FROM ";
    private static final String DELETE_SEQUENCE = "DELETE FROM SQLITE_SEQUENCE WHERE NAME = ";
    private static final String UPDATE_SEQUENCE = "UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME = ";


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
    public void addGPSData(String latitude, String longitude, float speed,
                           int weatherid, double rain, double wind,
                           double temp, double pressure, double humidity) {

        ContentValues values = new ContentValues(  );
        values.put( GPSData_latitude, latitude );
        values.put( GPSData_longitude, longitude );
        values.put( GPSData_speed, speed );
        values.put( GPSData_weatherid, weatherid );
        values.put( GPSData_rain, rain );
        values.put( GPSData_wind, wind );
        values.put( GPSData_temp, temp );
        values.put( GPSData_pressure, pressure );
        values.put( GPSData_humidity, humidity );
        values.put( COL_TIME, dateFormat.format( date.getTime() ) );
        getReadableDatabase().insert( TABLE_GPSData, null, values );
    }

    /**
     * Resets a sequence for a given table
     * @param tablename
     */
    public void resetSequenceForTable(String tablename) {
        try {
            getWritableDatabase().execSQL( UPDATE_SEQUENCE + "'" + tablename + "';" );
        } catch ( Exception ex ) {
            LogService.log( "Error updating sequence for table: " + ex.getMessage() );
        }
    }

    /**
     * Deletes all rows from the given table name.
     * This also resets the sequence created on the table, thereby resetting the
     * incremental count
     * @param tableName
     */
    public void deleteAllFromTable(String tableName) {
        try {
            getWritableDatabase().delete( tableName, null, null );
            getWritableDatabase().execSQL( UPDATE_SEQUENCE + "'" + tableName + "';" );
        } catch ( Exception ex ) {
            LogService.log( "Error deleting table: " + ex.getMessage() );
        }
    }

    /**
     * Deletes between the start and end IDs for a particular table.
     * @param tableName
     * @param start
     * @param end
     */
    public void deleteFromBetween(String tableName, String start, String end) {
        try {
            String query = "DELETE FROM " + tableName +
                    " WHERE " + COL_ID + " BETWEEN " + start + " and "+ end + ";";
            LogService.log( "QUERY: " + query );
            getWritableDatabase().execSQL( query );
        } catch ( Exception e ) {
            LogService.log( "Error deleting between: " + e.getMessage() );
        }
    }

    /**
     * Deletes all tables
     */
    public void deleteAll(){
        deleteAllFromTable( TABLE_GPSData );
        deleteAllFromTable( TABLE_SensorData );
    }

    /**
     * Drops and re-creates the table. This may be required if delete all from
     * table does not work.
     * @param tableName
     */
    public void dropTable(String tableName) {
        try {
            getWritableDatabase().execSQL( DROP_TABLE  + tableName);
        } catch ( Exception ex ) {
            LogService.log( "Error dropping table: " + ex.getMessage() );
        }
    }

    /**
     * Drops and recreates the following tables:
     * SensorData
     * GPSData
     */
    public void dropAndRecreateTables() {
        dropTable( TABLE_SensorData );
        dropTable( TABLE_GPSData );
        this.onCreate( getWritableDatabase() );
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
     * Selets the top n rows of the table
     * Orders by column id
     * @param table
     * @param row
     * @return
     */
    public JSONArray getDataAsJsonArray(String table, int row) {

        JSONArray resultSet = new JSONArray(  );
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + table + " ORDER BY " + COL_ID + " DESC LIMIT " + row + ";", null );
            cursor.moveToFirst();
            int i = 0, totalColumn;
            while (! cursor.isAfterLast()) {
                totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject(  );

                for (i=0; i<totalColumn; i++){
                    if (cursor.getColumnName( i ) != null) {
                        try {
                            if (cursor.getString( i ) != null) {
                                rowObject.put( cursor.getColumnName( i ), cursor.getString( i ) );
                            } else {
                                rowObject.put( cursor.getColumnName( i ), "" );
                            }
                        } catch ( Exception e ) {
                            LogService.log( "Error during building dataset from database: " + e.getMessage() );
                        }
                    }
                }
                resultSet.put( rowObject );
                cursor.moveToNext();
            }
        } catch ( Exception ex ) {
            LogService.log( "Exception occured while getting data in JSONArray format: " + ex.getMessage());
        }
        return resultSet;
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
