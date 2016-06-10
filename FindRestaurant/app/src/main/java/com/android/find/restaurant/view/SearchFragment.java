package com.android.find.restaurant.view;

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

import com.android.find.restaurant.R;
import com.android.find.restaurant.data.Restaurant;
import com.android.find.restaurant.view.helper.IconDownloader;
import com.android.find.restaurant.view.helper.RestaurantHolder;
import com.android.find.restaurant.view.helper.RestaurantsAdapter;
import com.android.find.restaurant.yelp.RestaurantFetcher;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches restaurants for the user and shows the list of same.
 * Created by rishi on 3/17/16.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String DIALOG_FILTER = "DialogFilter";
    private static final int REQUEST_FILTER_OPTION = 0;

    private LatLng mLocationLatLng;
    private RecyclerView mRestaurantRecyclerView;
    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private IconDownloader<RestaurantHolder> mIconDownloader;
    private int mSortByOption;
    private String mSearchTermText;
    private String mPlaceName;

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

        // Default search done for Downtown San Jose.
        //TODO Could be improved to search the current location.
        mSearchTermText = "All";
        mLocationLatLng = new LatLng(37.3341206839315, -121.884182650638);
        mSortByOption = 0;
        mPlaceName = "Downtown San Jose";
        fetchRestaurants();
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
            getActivity().setProgressBarIndeterminateVisibility(false);
            String toastMsg = mSearchTermText + " in " + mPlaceName;
            Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
        }
    }
}
