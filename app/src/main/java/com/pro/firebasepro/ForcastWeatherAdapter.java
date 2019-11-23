package com.pro.firebasepro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pro.firebasepro.forcastweather.ListWF;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import java.util.ArrayList;
public class ForcastWeatherAdapter extends RecyclerView.Adapter<ForcastWeatherAdapter.WeatherVH>{
    private List<ListWF> weatherLists = new ArrayList<>();
    private Context mContext;
    private ImageView imageViewf;
    public ForcastWeatherAdapter(Context mContext, List<ListWF> weatherLists) {
        this.weatherLists = weatherLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public WeatherVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.single_forcast_weather,viewGroup,false);
        return new WeatherVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherVH weatherVH, int i) {
        weatherVH.maxTempTV.setText("Max: "+weatherLists.get(i).getMain().getTempMax()+"°C");
        weatherVH.minTempTV.setText("Min: "+weatherLists.get(i).getMain().getTempMin()+"°C");
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE,i);

        String date = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
        String daysOfWeek = new SimpleDateFormat("EEEE").format(calendar.getTime());
        weatherVH.dateTV.setText(date);
        weatherVH.daysOfWeekTV.setText(daysOfWeek);

        String icon = weatherLists.get(i).getWeather().get(0)
                .getIcon();
        Picasso.get()
                .load(RetrofitClient.ICON_URL+icon+".png")
                .into(imageViewf);


    }

    @Override
    public int getItemCount() {
        return weatherLists.size();
    }

    class WeatherVH extends RecyclerView.ViewHolder{
        private TextView minTempTV, maxTempTV,dateTV,daysOfWeekTV;

        public WeatherVH(@NonNull View itemView) {
            super(itemView);
            maxTempTV = itemView.findViewById(R.id.single_temp_tv_fwf);
            minTempTV = itemView.findViewById(R.id.single_max_temp_tv_fwf);
            dateTV = itemView.findViewById(R.id.single_date_tv_fwf);
            daysOfWeekTV = itemView.findViewById(R.id.single_day_of_week_tv_fwf);
            imageViewf = itemView.findViewById(R.id.fcimg);
        }
    }

}