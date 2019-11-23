package com.pro.firebasepro.googlemap;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;
import com.pro.firebasepro.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleMapFragment extends Fragment {
    private Context context;
    private GeofencingClient geofencingClient;
    private GeofencingRequest.Builder geofencingRequest;
    private PendingIntent pendingIntent;
    private double latitudePos, longitudePos;
    private List<Geofence> geofenceList = new ArrayList<>();

    public GoogleMapFragment() {
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
        View view = inflater.inflate(R.layout.fragment_google_map, container, false);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("latitude") && bundle.containsKey("longitude")) {
            latitudePos = bundle.getDouble("latitude", 0);
            longitudePos = bundle.getDouble("longitude", 0);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        checkLocationPermission();
        geofencingClient = LocationServices.getGeofencingClient(context);
        Geofence bdbl = new Geofence.Builder()
                .setCircularRegion(
                        23.750854, 90.393527, 100)
                .setRequestId("BDBL Bhaban")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(6 * 60 * 60 * 1000)
                .build();
        geofenceList.add(bdbl);

        geofencingClient.addGeofences(getGeofencingRequest(), getPendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Geofence added successfully", Toast.LENGTH_SHORT).show();
                    }
                });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.clear(); //clear old markers
                LatLng bdbl = new LatLng(23.750854, 90.393527);
                mMap.addMarker(new MarkerOptions()
                        .position(bdbl)
                        .title("BDBL").snippet("12 Kazi Nazrul Islam Ave, Kawranbazar, Dhaka - 1215"));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bdbl, 14f));
                UiSettings uiSettings = mMap.getUiSettings();
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setMapToolbarEnabled(true);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitudePos,longitudePos))
                        .title("You").icon(bitmapDescriptorFromVector(getActivity(),R.drawable.spider)));

                /*CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(23.750854, 90.393527))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);*/

                /*mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(23.750854, 90.393527))
                        .title("Marker in BDBL").snippet("12 Kazi Nazrul Islam Ave, Kawranbazar, Dhaka - 1215")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.spider)));*/

                /*mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4629101,-122.2449094))
                        .title("Iron Man")
                        .snippet("His Talent : Plenty of money"));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.3092293,-122.1136845))
                        .title("Captain America"));
*/
            }
        });
        return view;
    }
    public boolean checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
            return false;
        }
        return true;
    }
    
    private GeofencingRequest getGeofencingRequest(){
        geofencingRequest = new GeofencingRequest.Builder();
        geofencingRequest.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        geofencingRequest.addGeofences(geofenceList);
        return geofencingRequest.build();
    }

    private PendingIntent getPendingIntent(){
        if(pendingIntent != null){
            return pendingIntent;
        }
        Intent intent = new Intent(context, GeofencingPendingIntentService.class);
        pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
/*
*/


