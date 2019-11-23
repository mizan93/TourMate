package com.pro.firebasepro;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ProgressDialog;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashScreenFragment extends Fragment {
    private Context context;
    private SplashScreenListener splashScreen;
    private ProgressBar progressBar;
    public SplashScreenFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        splashScreen = (SplashScreenListener) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentfragment
        View v = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        progressBar=v.findViewById(R.id.spprogressBar);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                splashScreen.onSplashScreenComplete();

            }
        }).start();
        return v;


    }
    @Override
    public void onActivityCreated(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    interface SplashScreenListener{
        void onSplashScreenComplete();

    }

}