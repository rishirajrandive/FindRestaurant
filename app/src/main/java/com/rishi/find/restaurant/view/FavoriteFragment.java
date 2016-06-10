package com.rishi.find.restaurant.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rishi.find.restaurant.R;
import com.rishi.find.restaurant.data.Restaurant;
import com.rishi.find.restaurant.view.helper.IconDownloader;
import com.rishi.find.restaurant.view.helper.RestaurantHolder;
import com.rishi.find.restaurant.view.helper.RestaurantsAdapter;
import com.rishi.find.restaurant.yelp.RestaurantFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to show the favourites for the user.
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
        View v = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        mRestaurantRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_restaurants_recycler_view);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new FetchFavorites().execute();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new FetchFavorites().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIconDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Background thread destroyed");
        mIconDownloader.quit();
    }

    /**
     * Sets the favourites fetched to the view.
     */
    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");
            mRestaurantRecyclerView.setAdapter(new RestaurantsAdapter(mRestaurantList, getActivity(), mIconDownloader, getActivity().getSupportFragmentManager()));
        }
    }

    /**
     * Class to fetch the favourites.
     */
    private class FetchFavorites extends AsyncTask<Void, Void, List<Restaurant>>{

        public FetchFavorites() {
        }

        @Override
        protected List<Restaurant> doInBackground(Void... params) {
            return RestaurantFetcher.getInstance(getActivity()).fetchFavorites(getActivity());
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
