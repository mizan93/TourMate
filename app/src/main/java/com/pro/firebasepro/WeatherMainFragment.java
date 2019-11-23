package com.pro.firebasepro;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherMainFragment extends Fragment {
    private static final String TAG = WeatherMainFragment.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private WeatherPagerAdapter weatherPagerAdapter;
    private FragmentManager manager;
    private List<Fragment> fragments  = new ArrayList<>();
    private CurrentWeatherFragment currentFragment;
    private ForecastWeatherFragment forecastFragment;
    private double latitude;
    private double longitude;
    private Context context;

    public WeatherMainFragment() {
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
        // Inflate the layout for this fragment
        //Log.e(TAG, "weather main fragment on create view called");
        View view = inflater.inflate(R.layout.fragment_weather_main, container, false);
        tabLayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout.addTab(tabLayout.newTab().setText("Current Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("Forecast Weather"));
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setTabTextColors(Color.WHITE, Color.YELLOW);

        manager = getChildFragmentManager();
        currentFragment = new CurrentWeatherFragment();
        forecastFragment = new ForecastWeatherFragment();

        fragments.add(currentFragment);
        fragments.add(forecastFragment);

       // pagerAdapter = new WeatherPagerAdapter(getActivity().getSupportFragmentManager());
        weatherPagerAdapter = new WeatherPagerAdapter(manager, fragments);
        viewPager.setAdapter(weatherPagerAdapter);

        viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
   private class WeatherPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();
        public WeatherPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = (ArrayList<Fragment>) fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("latitude") && bundle.containsKey("longitude")){
            latitude = bundle.getDouble("latitude",0);
            longitude = bundle.getDouble("longitude",0);
            currentFragment.getWeather(context, latitude, longitude);
            forecastFragment.getForcastWeather(context, latitude, longitude);
        }

    }

}
