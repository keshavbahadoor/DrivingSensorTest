package keshav.com.utilitylib;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Keshav on 2/28/2016.
 */
public class DateUtil {

    public static final long ONE_HOUR_MILISECONDS = 60*60*1000;

    /**
     * Checks if a date is before the current date
     * @param date date to check
     * @return
     */
    public static boolean dateHasPassed(String date) {

        try {
            Date strDate = ( new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss a" ) ).parse( date );
            if (new Date().after( strDate )) {
                return true;
            }
            return false;
        } catch ( Exception ex ) {
            LogService.log( "Exception occurred in dateHasPassed: " + ex.getMessage() );
            return true;
        }
    }

    /**
     * Checks if a date is before the current date
     * @param date
     * @param hour amount of hours to add to the current date
     * @return
     */
    public static boolean dateHasPassed(String date, int hour) {

        try {
            Date strDate = ( new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss a" ) ).parse( date );
            Date passDate = new Date();
            passDate.setTime( System.currentTimeMillis() + (hour * ONE_HOUR_MILISECONDS) );
            if (passDate.after( strDate )) {
                return true;
            }
            return false;
        } catch ( Exception ex ) {
            LogService.log( "Exception occurred in dateHasPassed: " + ex.getMessage() );
            return true;
        }
    }
}
