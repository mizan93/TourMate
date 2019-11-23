package com.pro.firebasepro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pro.firebasepro.connectioncheck.ConnectionCheckFragment;
import com.pro.firebasepro.googlemap.GoogleMapFragment;
import com.pro.firebasepro.nearbyplace.NearByPlaceAdapter;
import com.pro.firebasepro.nearbyplace.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  SplashScreenFragment.SplashScreenListener,
        LoginFragment.UserAuthListener, RegistrationFragment.UserAuthRegListener, EventAdapter.SingleItemActionListener,
        SingleItemListViewFragment.UserAuthSingleIteam, ImageViewAdapter.UserSingleImageClicked, NearByPlaceAdapter.OnSingleItemMapListener{
    private FragmentManager manager;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private SearchView searchView;
    private Geocoder geocoder;
    private List<Address> searchAddresses = new ArrayList<>();
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private FusedLocationProviderClient providerClient;
    private double latitude, longitude;
    //private MyConnectivityReceiver myConnectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geocoder = new Geocoder(this);
        Intent intent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        SplashScreenFragment ssf = new SplashScreenFragment();
        ft.add(R.id.fragmentContainer, ssf);
        ft.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        providerClient = LocationServices.getFusedLocationProviderClient(this);

        if(checkLocationPermission()){
            getDeviceLastLocation();
        }

    }

    private boolean checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_CODE);
            return false;
        }

        return true;
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLastLocation(){
        if(checkLocationPermission()){
            providerClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                //Toast.makeText(MainActivity.this, "Loc F: "+latitude+"\n"+longitude, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_LOCATION_PERMISSION_CODE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getDeviceLastLocation();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                super.onBackPressed();
            }

        //getSupportFragmentManager().popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                SearchRecentSuggestions suggestions =
                        new SearchRecentSuggestions(MainActivity.this,
                                MySuggestionsProvider.AUTHORITY,
                                MySuggestionsProvider.MODE);
                suggestions.saveRecentQuery(s, null);
                searchView.setQuery("", false);
                try {
                    searchAddresses = geocoder.getFromLocationName(s,1);
                    if (searchAddresses.size()>0) {
                        if (searchAddresses.get(0).hasLatitude() && searchAddresses.get(0).hasLongitude()){
                            Bundle bundle = new Bundle();
                            bundle.putDouble("latitude",searchAddresses.get(0).getLatitude());
                            bundle.putDouble("longitude",searchAddresses.get(0).getLongitude());
                            /*Toast.makeText(MainActivity.this, "Latitude: "
                                    +searchAddresses.get(0).getLatitude()+"\nLongitude: "
                                    +searchAddresses.get(0).getLongitude(), Toast.LENGTH_SHORT).show();*/

                            toolbar.setTitle("Weather Updates");
                            toolbar.setTitleTextColor(Color.WHITE);

                            FragmentTransaction ft = manager.beginTransaction();
                            WeatherMainFragment weatherMainFragment = new WeatherMainFragment();
                            weatherMainFragment.setArguments(bundle);
                            ft.replace(R.id.fragmentContainer, weatherMainFragment);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.commit();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(i);
                String query = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchView.setQuery(query,false);

                return false;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        switch (item.getItemId()){
            case R.id.menu_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation!");
                builder.setMessage("Do you want to exit application?");
                LayoutInflater inflater = LayoutInflater.from(this);

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.menu_logout:
                if(user != null){
                    auth.signOut();
                    getSupportActionBar().hide();
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    FragmentTransaction ftl = manager.beginTransaction();
                    LoginFragment logf = new LoginFragment();
                    ftl.replace(R.id.fragmentContainer, logf);
                    ftl.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ftl.commit();
                    //finish();
                }
                break;
            case R.id.action_feedback:
                toolbar.setTitle("Feedback");
                toolbar.setTitleTextColor(Color.WHITE);
                FragmentTransaction ft = manager.beginTransaction();
                FeedBackFragment feedBackFragment = new FeedBackFragment();
                ft.replace(R.id.fragmentContainer, feedBackFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            if(latitude!=0 && longitude!=0){
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude",latitude);
                bundle.putDouble("longitude",longitude);
                toolbar.setTitle("Google Map");
                toolbar.setTitleTextColor(Color.WHITE);
                FragmentTransaction ft = manager.beginTransaction();
                GoogleMapFragment googleMapFragment = new GoogleMapFragment();
                googleMapFragment.setArguments(bundle);
                ft.replace(R.id.fragmentContainer, googleMapFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
            else{
                Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();

            }

        }  else if (id == R.id.nav_home) {
            getSupportActionBar().show();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toolbar.setTitle("Home");
            toolbar.setTitleTextColor(Color.WHITE);
            FragmentTransaction ft = manager.beginTransaction();
            HomeFragment hmf = new HomeFragment();
            ft.replace(R.id.fragmentContainer, hmf);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

        } else if (id == R.id.nav_weather) {
            if(latitude!=0 && longitude!=0){
                //Toast.makeText(MainActivity.this, "FG: "+latitude+"\n"+longitude, Toast.LENGTH_SHORT).show();
                /*if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                }
*/
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude",latitude);
                bundle.putDouble("longitude",longitude);

                toolbar.setTitle("Weather Updates");
                toolbar.setTitleTextColor(Color.WHITE);

                FragmentTransaction ft = manager.beginTransaction();
                WeatherMainFragment weatherMainFragment = new WeatherMainFragment();

                weatherMainFragment.setArguments(bundle);
                ft.replace(R.id.fragmentContainer, weatherMainFragment);
                //ft.addToBackStack(null);
                /*manager.popBackStack();
                manager.popBackStackImmediate();*/
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();


            }
            else{
                Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();

            }


        } else if (id == R.id.nav_nearbyplace) {
            if(latitude!=0 && longitude!=0){
                //Toast.makeText(MainActivity.this, "FG: "+latitude+"\n"+longitude, Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putDouble("Latitude",latitude);
                bundle.putDouble("Longitude",longitude);

                toolbar.setTitle("Near By Places");
                toolbar.setTitleTextColor(Color.WHITE);
                FragmentTransaction ft = manager.beginTransaction();
                NearByPlaceFragment nearByPlaceFragment = new NearByPlaceFragment();
                nearByPlaceFragment.setArguments(bundle);
                ft.replace(R.id.fragmentContainer, nearByPlaceFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
            else{
                Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();

            }

        } else if (id == R.id.nav_share) {
            toolbar.setTitle("Connection Check");
            toolbar.setTitleTextColor(Color.WHITE);
            FragmentTransaction ft = manager.beginTransaction();
            ConnectionCheckFragment connectionCheckFragment = new ConnectionCheckFragment();
            ft.replace(R.id.fragmentContainer, connectionCheckFragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

        } else if (id == R.id.nav_send) {
            toolbar.setTitle("Feedback");
            toolbar.setTitleTextColor(Color.WHITE);
            FragmentTransaction ft = manager.beginTransaction();
            FeedBackFragment feedBackFragment = new FeedBackFragment();
            ft.replace(R.id.fragmentContainer, feedBackFragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onSplashScreenComplete() {
        getSupportActionBar().hide();
        FragmentTransaction ft = manager.beginTransaction();
        LoginFragment logf = new LoginFragment();
        ft.replace(R.id.fragmentContainer, logf);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onAuthComplete() {
        getSupportActionBar().show();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toolbar.setTitle("Home");
        toolbar.setTitleTextColor(Color.WHITE);

        FragmentTransaction ft = manager.beginTransaction();
        HomeFragment hmf = new HomeFragment();
        ft.replace(R.id.fragmentContainer, hmf);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }


    @Override
    public void onAuthRegComplete() {
        getSupportActionBar().hide();
        FragmentTransaction ft = manager.beginTransaction();
        LoginFragment rlogff = new LoginFragment();
        ft.replace(R.id.fragmentContainer, rlogff);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onAuthForRegComplete() {
        FragmentTransaction ft = manager.beginTransaction();
        RegistrationFragment regf = new RegistrationFragment();
        ft.replace(R.id.fragmentContainer, regf);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }


    @Override
    public void onSingleIteamListener(String rowId, String eventName, String startDate, String endDate, String budget) {

        toolbar.setTitle("Event Details");
        toolbar.setTitleTextColor(Color.WHITE);


        FragmentTransaction ft = manager.beginTransaction();
        SingleItemListViewFragment singleItemListViewFragment = new SingleItemListViewFragment();
        Bundle bnd = new Bundle();
        bnd.putString("rowId", rowId);
        bnd.putString("eventName", eventName);
        bnd.putString("startDate", startDate);
        bnd.putString("endDate", endDate);
        bnd.putString("budget", budget);
        singleItemListViewFragment.setArguments(bnd);
        ft.replace(R.id.fragmentContainer, singleItemListViewFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onAuthCapImage(String eventId) {
        FragmentTransaction ft = manager.beginTransaction();
        PhotoFragment photoFragment = new PhotoFragment();
        Bundle bnd = new Bundle();
        bnd.putString("eventId", eventId);
        photoFragment.setArguments(bnd);
        ft.replace(R.id.fragmentContainer, photoFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onAuthViewCapImage(String eventId) {
        toolbar.setTitle("All Image");
        toolbar.setTitleTextColor(Color.WHITE);
        FragmentTransaction ft = manager.beginTransaction();
        ImageFragment imageFragment = new ImageFragment();
        Bundle bnd = new Bundle();
        bnd.putString("eventId", eventId);
        imageFragment.setArguments(bnd);
        ft.replace(R.id.fragmentContainer, imageFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onAuthViewExpense(String eventId, String budget) {
        toolbar.setTitle("Expense Details");
        toolbar.setTitleTextColor(Color.WHITE);

        FragmentTransaction ft = manager.beginTransaction();
        ExpenseDetailsFragment expenseDetailsFragment = new ExpenseDetailsFragment();
        Bundle bnd = new Bundle();
        bnd.putString("eventId", eventId);
        bnd.putString("budget", budget);
        expenseDetailsFragment.setArguments(bnd);
        ft.replace(R.id.fragmentContainer, expenseDetailsFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onSingleImageClicked(String imageUrl) {
        FragmentTransaction ft = manager.beginTransaction();
        FullScreenImageFragment fullScreenImageFragment = new FullScreenImageFragment();
        Bundle bnd = new Bundle();
        bnd.putString("imageUrl", imageUrl);
        fullScreenImageFragment.setArguments(bnd);
        ft.replace(R.id.fragmentContainer, fullScreenImageFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void googleMapWithItemDetails(Result result) {

        if(latitude!=0 && longitude!=0){
            Bundle bundle = new Bundle();
            bundle.putDouble("latitude",latitude);
            bundle.putDouble("longitude",longitude);
            bundle.putSerializable("Result", result);
            toolbar.setTitle("Near By Place Details");
            toolbar.setTitleTextColor(Color.WHITE);
            FragmentTransaction ft = manager.beginTransaction();
            GoogleMapSingleItemFragment googleMapSingleItemFragment = new GoogleMapSingleItemFragment();

            googleMapSingleItemFragment.setArguments(bundle);
            ft.replace(R.id.fragmentContainer, googleMapSingleItemFragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else{
            Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();

        }
    }

/*    @Override
    protected void onResume() {
        super.onResume();
        myConnectivityReceiver = new MyConnectivityReceiver();
        registerReceiver(myConnectivityReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myConnectivityReceiver);
    }

    private class MyConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null && info.isConnected()){
                //Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(android.R.id.content), "Internet Connected", Snackbar.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(android.R.id.content), "Internet Disconnected", Snackbar.LENGTH_SHORT).show();

            }
        }
    }*/
}

