package keshav.com.drivingeventlib;

import android.content.Context;

import java.io.IOException;

import keshav.com.utilitylib.NetworkUtil;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Keshav on 6/6/2016.
 */
public class RetrofitServiceGenerator {


    /**
     * Basic OKHttp client
     */
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    /**
     * Builds retrofit object
     */
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl( GLOBALS.APPLICATION_SERVER )
                    .addConverterFactory( GsonConverterFactory.create()
                    );

    private static Retrofit.Builder weatherAPIBuilder =
            new Retrofit.Builder()
                    .baseUrl( GLOBALS.WEATHER_API_URL )
                    .addConverterFactory( GsonConverterFactory.create()
                    );


    /**
     * Creates API service for the specified class
     * @param serviceClass
     * @param <S>
     * @return
     */
    public static <S> S createService(Class<S> serviceClass) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Retrofit retrofit = builder.client( httpClient.addInterceptor( interceptor ).build() ).build();
        return retrofit.create( serviceClass );
    }

    public static <S> S createWeathjerService(Class<S> serviceClass) {
        Retrofit retrofit = weatherAPIBuilder.client( httpClient.build() ).build();
        return retrofit.create( serviceClass );
    }
}
