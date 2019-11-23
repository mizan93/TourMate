package com.pro.firebasepro;



import com.pro.firebasepro.currentweather.CurrentWeatherResponse;
import com.pro.firebasepro.forcastweather.ForecastWeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherService {
    @GET
    Call<CurrentWeatherResponse>getCurrentWeatherResponse(@Url String endUrl);

    @GET
    Call<ForecastWeatherResponse>getForcastWeatherResponse(@Url String endUrl);

}
