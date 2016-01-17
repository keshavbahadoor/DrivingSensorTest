package sensorlib;

import android.util.Log;

import java.math.BigDecimal;

/**
 * Created by Keshav on 1/10/2016.
 */
public class SensorFilter {

    private static final float APLHA = 0.25F;
    private static final int DECIMAL_PLACE = 3;
    private static BigDecimal bigDecimalBuffer;
    /**
     * Applys a low pass filter on given sensor reading array and returns the array
     * @param input
     * @return
     */
    public static float[] applyLowPassFilter(float[] input) {

        if (input != null) {
            float []temp = input.clone();
            for (int i=0; i<input.length; i++){
                temp[i] = temp[i] + APLHA * (input[i] - temp[i]);
            }
            return temp;
        }
        return input; 
    }

    /**
     * Applys a low pass filter as above.
     * This however then rounds the figure to the configured decimal space.
     * @param input
     * @return
     */
    public static float[] applyLowPassFilterRounded(float[] input){

        if (input != null) {
            float []temp = input.clone();
            for (int i=0; i<input.length; i++){
                temp[i] = temp[i] + APLHA * (input[i] - temp[i]);
                bigDecimalBuffer = new BigDecimal( Float.toString( temp[i] ) );
                bigDecimalBuffer = bigDecimalBuffer.setScale( DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP );
                temp[i] = bigDecimalBuffer.floatValue();
            }
            return temp;
        }
        return input;
    }
}
