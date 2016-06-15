package api;

import keshav.com.drivingeventlib.GLOBALS;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * API representation of server API.
 * Required for retrofit
 * Created by Keshav on 6/6/2016.
 */
public interface ServerAPI {

    /**
     * Adds GPS data
     * @param data object
     * @return response
     */
    @Headers({"X-API-KEY: " + GLOBALS.API_KEY})
    @FormUrlEncoded
    @POST("addgpsdatabulk")
    Call<Response> addGPSDataBulk( @Field( "userid" )String userid,
                                   @Field( "data" ) String data);

    /**
     * Adds Acceleration data
     * @param data object
     * @return response
     */
    @Headers({"X-API-KEY: " + GLOBALS.API_KEY})
    @FormUrlEncoded
    @POST("addaccsensordatabulk")
    Call<Response> addAccelerationDataBulk(@Field( "userid" )String userid,
                                           @Field( "data" ) String data);
}
