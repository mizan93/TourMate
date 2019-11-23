package com.pro.firebasepro;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static RetrofitClient retrofitClient;
    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String ICON_URL = "https://openweathermap.org/img/w/";

    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static WeatherService getService(){
        if(retrofitClient == null){
            retrofitClient = new RetrofitClient();
        }
        return retrofit.create(WeatherService.class);
    }
}
