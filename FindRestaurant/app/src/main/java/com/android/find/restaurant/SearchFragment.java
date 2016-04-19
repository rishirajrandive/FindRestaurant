package com.android.find.restaurant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.android.find.restaurant.view.helper.RestaurantsAdapter;
import com.android.find.restaurant.view.helper.RestaurantHolder;
import com.android.find.restaurant.yelp.RestaurantFetcher;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishi on 3/17/16.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final String DRAWER_CHOICE = "drawer_choice";
    public static final String DIALOG_FILTER_RESULTS = "filter_results";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_FILTER_OPTION = 0;

    private LatLng mLocationLatLng;
    private RecyclerView mRestaurantRecyclerView;
    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private IconDownloader<RestaurantHolder> mIconDownloader;
    private int mSortByOption = 0;
    private String mSearchTermText;

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
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        restaurantHolder.bindDrawable(drawable);
                    }
                }
        );
        mIconDownloader.start();
        mIconDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View v = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        //int i = getArguments().getInt(DRAWER_CHOICE);
        //Log.d(TAG, "Fragment value received "+i);
        mRestaurantRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_restaurants_recycler_view);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        setupAdapter();

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

                getActivity().setProgressBarIndeterminateVisibility(true);
                searchView.setIconified(true);
                searchView.clearFocus();
                mSearchTermText = s;
                new FetchRestaurants("tacos", 37.3352630, -121.8848328, 0).execute();
                //fetchRestuarants();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Search clicked");
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                Log.d(TAG, "Search selected");
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_filter:
                Log.d(TAG, "Filter results");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FilterResultFragment dialog = FilterResultFragment
                        .newInstance(mSortByOption);
                dialog.setTargetFragment(SearchFragment.this, REQUEST_FILTER_OPTION);

                dialog.show(fragmentManager, DIALOG_DATE);
                return true;
            case R.id.menu_place_pick:
                Log.d(TAG, "place picker selected");
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try{
                    startActivityForResult(builder.build(getContext()), PLACE_PICKER_REQUEST);

                }catch (Exception ex){
                    Log.d(TAG, "Exception is getting the request");
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            Log.d(TAG, "Result code " + resultCode);
            if(resultCode == Activity.RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, getContext());
                String toastMsg = String.format("Place: %s", place.getName());
                mLocationLatLng = place.getLatLng();

                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();

            }
        }
        if(requestCode == REQUEST_FILTER_OPTION){
            if(resultCode == Activity.RESULT_OK){
                mSortByOption = (int) data.getSerializableExtra(FilterResultFragment.EXTRA_DATE);
                fetchRestuarants();;

            }else if(resultCode == Activity.RESULT_CANCELED){
                mSortByOption = (int) data.getSerializableExtra(FilterResultFragment.EXTRA_DATE);
            }
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");
            mRestaurantRecyclerView.setAdapter(new RestaurantsAdapter(mRestaurantList, getActivity(), mIconDownloader, getActivity().getSupportFragmentManager()));
        }
    }

    private void fetchRestuarants(){
        new FetchRestaurants(mSearchTermText, mLocationLatLng.latitude, mLocationLatLng.longitude, mSortByOption).execute();
    }

    private class FetchRestaurants extends AsyncTask<Void, Void, List<Restaurant>>{

        private String mTerm;
        private double mLatitude;
        private double mLongitude;
        private int mSortOption;

        public FetchRestaurants(String term, double latitude, double longitude, int sortOption) {
            mTerm = term;
            mLatitude = latitude;
            mLongitude = longitude;
            mSortOption = sortOption;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected List<Restaurant> doInBackground(Void... params) {

            return RestaurantFetcher.get(getActivity()).search(mTerm, mLatitude, mLongitude, mSortOption);
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            super.onPostExecute(restaurants);
            mRestaurantList = restaurants;
            setupAdapter();
            getActivity().setProgressBarIndeterminateVisibility(false);
        }


    }
}
