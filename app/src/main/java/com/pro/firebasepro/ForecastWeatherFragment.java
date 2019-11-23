package com.pro.firebasepro;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pro.firebasepro.forcastweather.ForecastWeatherResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastWeatherFragment extends Fragment {
    private static final String TAG = ForecastWeatherFragment.class.getSimpleName();
    private String unit = "metric";
    private RecyclerView recyclerView;
    private List<com.pro.firebasepro.forcastweather.ListWF> lists = new ArrayList<>();
    private Context context;
    private LinearLayoutManager linearLayoutManager;

    public ForecastWeatherFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
        recyclerView = view.findViewById(R.id.rv_fwf);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }


    public void getForcastWeather(final Context context, double lat, double lng) {
        this.context = context;
        getForcastWeatherUpdate(lat, lng);
    }

    public void getForcastWeatherUpdate(double lat, double lng) {

        String api = context.getString(R.string.weather_api);
        //String api = getResources().getString(R.string.weather_api);
        String endUrl = String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s",lat,lng,unit,api);
        RetrofitClient.getService().getForcastWeatherResponse(endUrl)
                .enqueue(new Callback<ForecastWeatherResponse>() {
                    @Override
                    public void onResponse(Call<ForecastWeatherResponse> call, Response<ForecastWeatherResponse> response) {
                        if(response.isSuccessful()){
                            ForecastWeatherResponse forecastWeatherResponse = response.body();
                            lists = forecastWeatherResponse.getList();
                            //Toast.makeText(context, "size is "+lists.size(), Toast.LENGTH_SHORT).show();
                            ForcastWeatherAdapter forcastWeatherAdapter = new ForcastWeatherAdapter(context,lists);
                            recyclerView.setAdapter(forcastWeatherAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastWeatherResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage());
                        Snackbar.make(getView(), t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

    }

}
