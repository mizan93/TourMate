package com.pro.firebasepro;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.pro.firebasepro.R;
import com.pro.firebasepro.nearbyplace.NearByPlaceAdapter;
import com.pro.firebasepro.nearbyplace.NearByPlacesResponse;
import com.pro.firebasepro.nearbyplace.Result;
import com.pro.firebasepro.nearbyplace.RetrofitClientNearByPlace;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearByPlaceFragment extends Fragment {
    private ArrayList<String> types = new ArrayList<>();
    private ArrayList<String> radiouses = new ArrayList<>();
    private ArrayAdapter<String> typesAdapter;
    private ArrayAdapter<String> radiousesAdapter;
    private Spinner typeSP, radiousSP;
    private ImageButton find;
    private RecyclerView rv;
    private LinearLayoutManager manager;
    private Context context;
    private double latitude;
    private double longitude;
    private String rradius;
    private String ttype;
    private List<Result> viewResult = new ArrayList<>();

    public NearByPlaceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_near_by_place, container, false);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("Latitude") && bundle.containsKey("Longitude")){
            latitude = bundle.getDouble("Latitude",0);
            longitude = bundle.getDouble("Longitude",0);
            //Toast.makeText(context, "Latitude: "+latitude+"\nLongitude: "+longitude, Toast.LENGTH_SHORT).show();
        }
        getSpinnersList();
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        typeSP = view.findViewById(R.id.type_spinner);
        radiousSP = view.findViewById(R.id.radius_spinner);
        rv = view.findViewById(R.id.rv_nearplace);
        find = view.findViewById(R.id.btnfind);

        typesAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line, types);
        radiousesAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line, radiouses);
        typeSP.setAdapter(typesAdapter);
        radiousSP.setAdapter(radiousesAdapter);
        rv.setLayoutManager(manager);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rradius = radiousSP.getSelectedItem().toString();
                ttype = typeSP.getSelectedItem().toString();
                String api = context.getString(R.string.google_maps_api);
                String endUrl = String.format("place/nearbysearch/json?location=%f,%f&radius=%s&type=%s&key=%s",latitude,longitude,rradius,ttype,api);
                RetrofitClientNearByPlace.getService().getNearByPlacesResponse(endUrl)
                        .enqueue(new Callback<NearByPlacesResponse>() {
                            @Override
                            public void onResponse(Call<NearByPlacesResponse> call, Response<NearByPlacesResponse> response) {
                                if(response.isSuccessful()){
                                    NearByPlacesResponse nearByPlacesResponse = response.body();
                                    List<Result> results = nearByPlacesResponse.getResults();
                                    viewResult.addAll(results);
                                    NearByPlaceAdapter nearByPlaceAdapter = new NearByPlaceAdapter(getActivity(),results);
                                    rv.setAdapter(nearByPlaceAdapter);
                                    rv.setLayoutManager(manager);


                                }
                            }

                            @Override
                            public void onFailure(Call<NearByPlacesResponse> call, Throwable t) {
                                Snackbar.make(getView(), "onFailure"+t.getMessage(), Snackbar.LENGTH_LONG).show();
                                //Log.e(TAG, "onFailure: "+t.getMessage());
                            }
                        });


            }
        });

        return view;
    }

    private void getSpinnersList(){
        types.add("restaurant");
        types.add("shopping_mall");
        types.add("bank");
        types.add("atm");
        types.add("bus_station");
        types.add("hospital");
        types.add("mosque");
        types.add("police");

        radiouses.add("100");
        radiouses.add("200");
        radiouses.add("300");
        radiouses.add("500");
        radiouses.add("1000");
        radiouses.add("1500");
        radiouses.add("2000");
        radiouses.add("2500");
        radiouses.add("3000");
        radiouses.add("4000");
        radiouses.add("5000");
        radiouses.add("6000");
        radiouses.add("20000");

    }
}
