package com.pro.firebasepro.nearbyplace;

import com.pro.firebasepro.RetrofitClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientNearByPlace {
    private static Retrofit retrofit;
    private static RetrofitClientNearByPlace retrofitClient;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private RetrofitClientNearByPlace(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NearByPlaceService getService(){
        if(retrofitClient == null){
            retrofitClient = new RetrofitClientNearByPlace();
        }
        return retrofit.create(NearByPlaceService.class);
    }
}
