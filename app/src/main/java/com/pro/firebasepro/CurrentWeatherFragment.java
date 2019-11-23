package com.pro.firebasepro;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pro.firebasepro.currentweather.CurrentWeatherResponse;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment {
    private Context context;
    private String unit = "metric";
    private double nLat, nLng;
    private ToggleButton cf;
    private String temp, min, max;
    double CtoF, FtoC, fmin, fmax;
    private TextView mTempTV, mCityTV, mHumidity, mDate, mDayOfWeek, minmax, sunriseset,des;
    private ImageView imageView;
    private DecimalFormat form;
    private static final String TAG = CurrentWeatherFragment.class.getSimpleName();
    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        form = new DecimalFormat("0.00");
        mTempTV = view.findViewById(R.id.temp_tv_cwf);
        mCityTV = view.findViewById(R.id.city_tv_cwf);
        mHumidity = view.findViewById(R.id.humidity_pressure_tv_cwf);
        mTempTV = view.findViewById(R.id.temp_tv_cwf);
        mCityTV = view.findViewById(R.id.city_tv_cwf);
        mDate = view.findViewById(R.id.date_tv_cwf);
        mDayOfWeek = view.findViewById(R.id.day_of_week_tv_cwf);
        imageView = view.findViewById(R.id.icon_img_cwf);
        minmax = view.findViewById(R.id.min_max_temp_tv_cwf);
        sunriseset = view.findViewById(R.id.sunrise_sunset_tv_cwf);
        des = view.findViewById(R.id.descriptiontv);
        cf = view.findViewById(R.id.unitSwitch);
        cf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // if toggle button is enabled/on
                    unit="metric";
                    //FtoC = (CtoF-32) * (5/9);
                    mTempTV.setText(""+temp+"°C");
                    minmax.setText("Min: "+min+"°C"+"\nMax: "+max+"°C");
                }
                else{
                    // If toggle button is disabled/off
                    unit="imperial";
                    CtoF = (Double.valueOf(temp)* 1.8) + 32;
                    fmin = (Double.valueOf(min)* 1.8) + 32;
                    fmax = (Double.valueOf(max)* 1.8) + 32;
                    mTempTV.setText(""+form.format(CtoF)+"°F");
                    minmax.setText("Min: "+form.format(fmin)+"°F"+"\nMax: "+form.format(fmax)+"°F");
                }
            }
        });

        return view;
    }

    public void getWeather(final Context context, double lat, double lng) {
        this.context = context;
        if(lat!=0 || lng !=0){
            getWeatherUpdate(lat, lng);
        }else{
            Toast.makeText(context, "Null Value!", Toast.LENGTH_SHORT).show();

        }
    }

/*   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTempTV = view.findViewById(R.id.temp_tv_cwf);
        mCityTV = view.findViewById(R.id.city_tv_cwf);
        mHumidity = view.findViewById(R.id.humidity_pressure_tv_cwf);
        mTempTV = view.findViewById(R.id.temp_tv_cwf);
        mCityTV = view.findViewById(R.id.city_tv_cwf);
        mDate = view.findViewById(R.id.date_tv_cwf);
        mDayOfWeek = view.findViewById(R.id.day_of_week_tv_cwf);
        imageView = view.findViewById(R.id.icon_img_cwf);
        minmax = view.findViewById(R.id.min_max_temp_tv_cwf);
        sunriseset = view.findViewById(R.id.sunrise_sunset_tv_cwf);
        des = view.findViewById(R.id.descriptiontv);
        cf = view.findViewById(R.id.unitSwitch);
        if (cf.isChecked()){
            unit="metric";
            Toast.makeText(context, "Unit: "+unit, Toast.LENGTH_SHORT).show();
            //getWeatherUpdate(lat, lng, unit);
        } else{
            unit="imperial";
            Toast.makeText(context, "Unit: "+unit, Toast.LENGTH_SHORT).show();
            //getWeatherUpdate(lat, lng, unit);
        }
    }*/

    public void getWeatherUpdate(double lat, double lng) {

        String api = context.getString(R.string.weather_api);
        //String api = getResources().getString(R.string.weather_api);

        String endUrl = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",lat, lng, unit, api);
        //Toast.makeText(context, "endUrl: "+endUrl, Toast.LENGTH_SHORT).show();
        RetrofitClient.getService().getCurrentWeatherResponse(endUrl)
                .enqueue(new Callback<CurrentWeatherResponse>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                        if(response.isSuccessful()){
                            //Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                            CurrentWeatherResponse currentWeatherResponse =
                                    response.body();
                            if (currentWeatherResponse.getMain().getTemp() != null ||
                                    currentWeatherResponse.getMain().getHumidity()!=null ||
                                    currentWeatherResponse.getSys().getCountry()!=null ||
                                    currentWeatherResponse.getWeather().get(0).getDescription()!=null &&
                                    currentWeatherResponse.getSys().getSunrise()!=null ||
                                    currentWeatherResponse.getSys().getSunset()!=null ||
                                    currentWeatherResponse.getName()!=null ||
                                    currentWeatherResponse.getMain().getTempMax()!=null ||
                                    currentWeatherResponse.getMain().getTempMin()!=null){
                                temp = String.valueOf(currentWeatherResponse.getMain().getTemp());
                                String city = currentWeatherResponse.getName();
                                String country = currentWeatherResponse.getSys().getCountry();
                                String description = currentWeatherResponse.getWeather().get(0).getDescription();
                                max = String.valueOf(currentWeatherResponse.getMain().getTempMax());
                                min = String.valueOf(currentWeatherResponse.getMain().getTempMin());

                                mTempTV.setText(""+temp+"°C");
                                mHumidity.setText("Humidity: " + currentWeatherResponse.getMain().getHumidity());
                                mCityTV.setText("City: " +city+"\nCountry: "+country);
                                des.setText("Description: "+description);
                                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                mDate.setText(date);
                                String dayOfWeek = new SimpleDateFormat("EEEE").format(new Date());
                                mDayOfWeek.setText(dayOfWeek);
                                minmax.setText("Min: "+min+"°C"+"\nMax: "+max+"°C");
                                int sunrise = currentWeatherResponse.getSys().getSunrise();
                                int sunset = currentWeatherResponse.getSys().getSunset();
                                String sunrisetime = new SimpleDateFormat("hh:mm:ss aa").format(new Time(sunrise));
                                String sunsettime = new SimpleDateFormat("hh:mm:ss aa").format(new Time(sunset));
                                sunriseset.setText("Sunrise: "+sunrisetime+"\nSunset: "+sunsettime);
                                String icon = currentWeatherResponse.getWeather().get(0)
                                        .getIcon();
                                Picasso.get()
                                        .load(RetrofitClient.ICON_URL+icon+".png")
                                        .into(imageView);
                            } else {
                                Toast.makeText(context, "Null value", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                        Toast.makeText(context, "onFailure"+t.getMessage(), Toast.LENGTH_SHORT).show();

                        //Log.e(TAG, "onFailure: "+t.getMessage());
                    }
                });
    }

}
