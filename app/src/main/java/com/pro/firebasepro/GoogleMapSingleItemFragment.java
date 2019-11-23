package com.pro.firebasepro;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pro.firebasepro.nearbyplace.Photo;
import com.pro.firebasepro.nearbyplace.Result;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleMapSingleItemFragment extends Fragment {
    private double latitudePos, longitudePos;
    private Context context;
    private LinearLayout mItemLinearLayout;
    private ImageView itemIMG;
    private TextView itemNameTV;
    private TextView itemLocationTV;
    public GoogleMapSingleItemFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_map_single_item, container, false);
        //showPlacesBT = view.findViewById(R.id.show_places_bt_mgf);
        mItemLinearLayout = view.findViewById(R.id.item_ll_mgf);
        itemIMG = view.findViewById(R.id.item_img_mgf);
        itemNameTV = view.findViewById(R.id.item_name_tv_mgf);
        itemLocationTV = view.findViewById(R.id.item_location_tv_mgf);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("Result") && bundle.containsKey("latitude") && bundle.containsKey("longitude")) {
            latitudePos = bundle.getDouble("latitude", 0);
            longitudePos = bundle.getDouble("longitude", 0);
            final Result result = (Result) bundle.getSerializable("Result");
            mItemLinearLayout.setVisibility(View.VISIBLE);
            if (result != null) {
                String urlPrefix = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
                List<Photo> photos = result.getPhotos();
                String apiKey = getString(R.string.google_maps_api);

                if(photos!=null){
                    String photoref = photos.get(0).getPhotoReference();
                    String photoUrl = String.format("%s%s&key=%s", urlPrefix, photoref, apiKey);
                    Picasso.get().load(photoUrl).into(itemIMG);
                }else{
                    Toast.makeText(context,"Image Not Found", Toast.LENGTH_SHORT).show();
                }

                itemNameTV.setText(result.getName());
                itemLocationTV.setText(result.getVicinity());
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng llatllong = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                        mMap.addMarker(new MarkerOptions().position(llatllong).title(result.getName()).snippet(result.getVicinity()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llatllong, 14f));
                        UiSettings uiSettings = mMap.getUiSettings();
                        uiSettings.setZoomControlsEnabled(true);
                        uiSettings.setMapToolbarEnabled(true);

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitudePos,longitudePos))
                                .title("You"));

                    }
                });

            }

        }

        return view;

    }

}
