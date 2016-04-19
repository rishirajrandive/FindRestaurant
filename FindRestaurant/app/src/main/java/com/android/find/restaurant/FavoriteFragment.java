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

import com.android.find.restaurant.data.RestaurantDbHelper;
import com.android.find.restaurant.view.helper.RestaurantHolder;
import com.android.find.restaurant.view.helper.RestaurantsAdapter;
import com.android.find.restaurant.yelp.RestaurantFetcher;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishi on 3/17/16.
 */
public class FavoriteFragment extends Fragment {

    private static final String TAG = "FavoriteFragment";

    private RecyclerView mRestaurantRecyclerView;
    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private IconDownloader<RestaurantHolder> mIconDownloader;

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
        mRestaurantRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_restaurants_recycler_view);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new FetchFavorites().execute();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        Log.d(TAG, "Menu inflated");
        menuInflater.inflate(R.menu.main, menu);
    }

    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");
            mRestaurantRecyclerView.setAdapter(new RestaurantsAdapter(mRestaurantList, getActivity(), mIconDownloader, getActivity().getSupportFragmentManager()));
        }
    }

    private class FetchFavorites extends AsyncTask<Void, Void, List<Restaurant>>{

        public FetchFavorites() {
        }

        @Override
        protected List<Restaurant> doInBackground(Void... params) {

            return RestaurantFetcher.get(getActivity()).fetchFavorites(getActivity());
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
