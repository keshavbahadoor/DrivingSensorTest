package keshav.com.utilitylib;

import android.util.Log;

import junit.framework.TestCase;

/**
 * Created by Keshav on 2/29/2016.
 */
public class DateUtilTest extends TestCase {


    public void testDateHasPassed() {
        Log.d("TEST", "Starting test date");
        System.out.println("Testing date");
        String date = "2016-02-29 10:11:23 PM";
        assertEquals( true, DateUtil.dateHasPassed( date ) );
    }

    public void testDateHasPassed2() {
        System.out.println("Testing date");
        String date = "2016-02-29 10:11:23 PM";
        assertEquals( false, DateUtil.dateHasPassed( date, 2 ) );
    }

}
