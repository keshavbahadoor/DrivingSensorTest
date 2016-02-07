package sensorlib;

import java.math.BigDecimal;

/**
 * Created by Keshav on 1/10/2016.
 */
public class SensorFilter {

    private static final float LOW_PASS_APLHA = 0.25F;
    private static final float HIGH_PASS_ALPHA = 0.8F;
    private static final int DECIMAL_PLACE = 3;
    private static BigDecimal bigDecimalBuffer;
    private static float [] gravityBuffer = {9.8F, 9.8F, 9.8F, 9.8F, 9.8F, 9.8F};
    private static float [] gravityBufferRound = {9.8F, 9.8F, 9.8F, 9.8F, 9.8F, 9.8F};

    /**
     * Applys a low pass filter on given sensor reading array and returns the array
     * @param input
     * @return
     */
    public static float[] applyLowPassFilter(float[] input) {

        if (input != null) {
            float []temp = input.clone();
            for (int i=0; i<input.length; i++){
                temp[i] = temp[i] + LOW_PASS_APLHA * (input[i] - temp[i]);
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
                temp[i] = temp[i] + LOW_PASS_APLHA * (input[i] - temp[i]);
                bigDecimalBuffer = new BigDecimal( Float.toString( temp[i] ) );
                bigDecimalBuffer = bigDecimalBuffer.setScale( DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP );
                temp[i] = bigDecimalBuffer.floatValue();
            }
            return temp;
        }
        return input;
    }

    /**
     * Rounds the values to the configured decimal space
     * @param input
     * @return
     */
    public static float[] applyRoundFilter(float[] input){

        if (input != null) {
            for (int i=0; i<input.length; i++){
                bigDecimalBuffer = new BigDecimal( Float.toString( input[i] ) );
                bigDecimalBuffer = bigDecimalBuffer.setScale( DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP );
                input[i] = bigDecimalBuffer.floatValue();
            }
        }
        return input;
    }

    /**
     * Applies a high pass filter on given sensor value array.
     * This is used to eliminate gravity :
     * in order to measure the real acceleration of the device, the contribution of the force of gravity must be eliminated
     *
     *  This implementation uses a static gravity buffer. Function relies on previous values of gravity, and
     *  a new array cannot be instantiated at each call
     * Reference: http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     * @param input
     * @return
     */
    public static float[] applyHighPassFilter(float[] input) {

        if (input != null) {
            for (int i=0; i<input.length; i++) {
                gravityBuffer[i] = HIGH_PASS_ALPHA * gravityBuffer[i] + ( 1 - HIGH_PASS_ALPHA) * input[i];
                input[i] = input[i] - gravityBuffer[i];
            }
        }
        return input;
    }

    /**
     * Applies a high pass filter as above (see above method description)
     * this however, rounds the values to the configured decimal place.
     * A separate buffer is used.
     *
     * Code is repeated to maximize performance.
     * @param input
     * @return
     */
    public static float[] applyHighPassFilterRounded(float[] input) {

        if (input != null) {
            for (int i=0; i<input.length; i++) {
                gravityBufferRound[i] = HIGH_PASS_ALPHA * gravityBufferRound[i] + ( 1 - HIGH_PASS_ALPHA) * input[i];
                input[i] = input[i] - gravityBufferRound[i];
                bigDecimalBuffer = new BigDecimal( Float.toString( input[i] ) );
                bigDecimalBuffer = bigDecimalBuffer.setScale( DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP );
                input[i] = bigDecimalBuffer.floatValue();
            }
        }
        return input;
    }
}
















