package com.rishi.find.restaurant.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.rishi.find.restaurant.R;
import com.rishi.find.restaurant.data.Restaurant;
import com.rishi.find.restaurant.view.helper.IconDownloader;
import com.rishi.find.restaurant.view.helper.RestaurantHolder;
import com.rishi.find.restaurant.view.helper.RestaurantsAdapter;
import com.rishi.find.restaurant.yelp.RestaurantFetcher;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Searches restaurants for the user and shows the list of same.
 * Created by rishi on 3/17/16.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String DIALOG_FILTER = "DialogFilter";
    private static final int REQUEST_FILTER_OPTION = 0;
    private static final String SEARCH_TERM = "search_term";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String SORT_OPTION = "sort_option";

    private LatLng mLocationLatLng;
    private RecyclerView mRestaurantRecyclerView;
    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private IconDownloader<RestaurantHolder> mIconDownloader;
    private int mSortByOption;
    private String mSearchTermText;
    private String mPlaceName;
    private LocationManager mLocationManager;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Log.d(TAG, "On create called");

        Handler responseHandler = new Handler();
        mIconDownloader = new IconDownloader<>(responseHandler);
        mIconDownloader.setIconDownloadListener(
                new IconDownloader.IconDownloadListener<RestaurantHolder>() {
                    @Override
                    public void onIconDownloaded(RestaurantHolder restaurantHolder, Bitmap bitmap) {
                        if(isAdded()){
                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                            restaurantHolder.bindDrawable(drawable);
                        }
                    }
                }
        );
        mIconDownloader.start();
        mIconDownloader.getLooper();

        initiateSearch();
    }

    private void initiateSearch(){
        // Default search done for Downtown San Jose.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(!preferences.getString(LATITUDE, "").equalsIgnoreCase("")){
            mSearchTermText = preferences.getString(SEARCH_TERM, "All");
            mLocationLatLng = new LatLng(Double.parseDouble(preferences.getString(LATITUDE, "37.322993")), Double.parseDouble(preferences.getString(LONGITUDE, "-121.883200")));
            mSortByOption = preferences.getInt(SORT_OPTION, 0);

        }else {
            mSearchTermText = "All";
            mLocationLatLng = getCurrentLocation();
            mSortByOption = 0;
            saveSearch();
        }

        mPlaceName = "";
        new PlaceNameTask().execute();
        fetchRestaurants();
    }

    private void saveSearch(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.edit().putString(SEARCH_TERM, mSearchTermText).apply();
        preferences.edit().putString(LATITUDE, mLocationLatLng.latitude + "").apply();
        preferences.edit().putString(LONGITUDE, mLocationLatLng.longitude + "").apply();
        preferences.edit().putInt(SORT_OPTION, mSortByOption).apply();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View v = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        mRestaurantRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_restaurants_recycler_view);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIconDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIconDownloader.quit();
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        Log.d(TAG, "Menu inflated");
        menuInflater.inflate(R.menu.fragment_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSubmitButtonEnabled(true);
        int searchIconId = android.support.v7.appcompat.R.id.search_go_btn;
        ImageView searchIcon = (ImageView) searchView.findViewById(searchIconId);
        searchIcon.setImageResource(R.drawable.action_search);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d(TAG, "Focus removed");
                    searchView.setIconified(true);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                searchView.setIconified(true);
                searchView.clearFocus();
                mSearchTermText = s;
                fetchRestaurants();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_filter:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FilterResultFragment dialog = FilterResultFragment
                        .newInstance(mSortByOption);
                dialog.setTargetFragment(SearchFragment.this, REQUEST_FILTER_OPTION);

                dialog.show(fragmentManager, DIALOG_FILTER);
                return true;
            case R.id.menu_place_pick:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try{
                    startActivityForResult(builder.build(getContext()), PLACE_PICKER_REQUEST);

                }catch (Exception ex){
                    Log.d(TAG, "Exception in Place picker "+ ex);
                    Toast.makeText(getContext(), "Error fetching place, please retry", Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if(resultCode == Activity.RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, getContext());
                mPlaceName = String.format("%s", place.getName());
                mLocationLatLng = place.getLatLng();
                mSearchTermText = "All";
                fetchRestaurants();
            }
        }
        if(requestCode == REQUEST_FILTER_OPTION){
            if(resultCode == Activity.RESULT_OK){
                mSortByOption = (int) data.getSerializableExtra(FilterResultFragment.EXTRA_OPTION);
                fetchRestaurants();;

            }else if(resultCode == Activity.RESULT_CANCELED){
                mSortByOption = (int) data.getSerializableExtra(FilterResultFragment.EXTRA_OPTION);
            }
        }
    }

    /**
     * Sets adapter for the @RecyclerView used for showing the list of restaurants.
     */
    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");
            mRestaurantRecyclerView.setAdapter(new RestaurantsAdapter(mRestaurantList, getActivity(), mIconDownloader, getActivity().getSupportFragmentManager()));
        }
    }

    /**
     * Fetching restaurants by calling the search of @RestaurantFetcher in @AsyncTask when place is chosen or for default search
     */
    private void fetchRestaurants(){
        showProgressDialog();
        new FetchRestaurants().execute();
    }

    private class FetchRestaurants extends AsyncTask<Void, Void, List<Restaurant>>{

        public FetchRestaurants() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected List<Restaurant> doInBackground(Void... params) {
            List<Restaurant> restaurantList = new ArrayList<>();
            try{
                restaurantList =RestaurantFetcher.getInstance(getActivity()).search(mSearchTermText, mLocationLatLng.latitude, mLocationLatLng.longitude, mSortByOption);
            }catch (JSONException ex){
                Log.d(TAG, "JSON Parse Exception occurred while fetching data from Yelp API " + ex);
            }
            return restaurantList;
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            super.onPostExecute(restaurants);
            mRestaurantList = restaurants;
            setupAdapter();
            hideProgressDialog();
            if(mRestaurantList.size() > 0){
                String toastMsg = mSearchTermText + " in " + mPlaceName;
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
                saveSearch();
            }else {
                String toastMsg = "Sorry! no results, try another location";
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private LatLng getCurrentLocation() {
        mLocationManager =(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String locationProvider = mLocationManager.getBestProvider(criteria, true);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            return new LatLng(37.322993, -121.883200);
        }

        if(isLocationServiceEnabled()){
            android.location.Location location = mLocationManager.getLastKnownLocation(locationProvider);
            if(location != null){
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                return userLocation;
            }
        }else {
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        }
        return new LatLng(37.322993, -121.883200);
    }

    public boolean isLocationServiceEnabled(){
        boolean location_service_enabled = false;
        boolean network_enabled = false;

        if(mLocationManager != null) {
            try{
                location_service_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }catch(Exception ex){
                Log.e(TAG, "Error get gps provider "+ ex.getStackTrace());
            }

            try{
                network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }catch(Exception ex){
                Log.e(TAG, "Error get network provider "+ ex.getStackTrace());
            }
        }
        return location_service_enabled || network_enabled;
    }

    private class PlaceNameTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(mLocationLatLng.latitude, mLocationLatLng.longitude, 1);
                if(addresses.size() > 0){
                    return addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching place name " + e.getStackTrace());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mPlaceName = s;
        }
    }

    private void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Loading restaurants...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }
}
