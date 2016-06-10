package com.rishi.find.restaurant.view.helper;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rishi.find.restaurant.R;
import com.rishi.find.restaurant.data.Restaurant;

import java.util.List;

/**
 * Created by rishi on 3/23/16.
 */
public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantHolder> {

    private List<Restaurant> mRestaurants;

    private Activity mActivity;

    private IconDownloader<RestaurantHolder> mIconDownloader;

    private FragmentManager mFragmentManager;

    public RestaurantsAdapter(List<Restaurant> restaurants, Activity activity, IconDownloader<RestaurantHolder> iconDownloader, FragmentManager fragmentManager) {
        mRestaurants = restaurants;
        mActivity = activity;
        mIconDownloader = iconDownloader;
        mFragmentManager = fragmentManager;
    }

    @Override
    public RestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View view = layoutInflater.inflate(R.layout.restaurant_list, parent, false);

        return new RestaurantHolder(view, mActivity, mFragmentManager);
    }

    @Override
    public void onBindViewHolder(RestaurantHolder holder, int position) {
        Restaurant restaurant = mRestaurants.get(position);
        Drawable placeholder = mActivity.getResources().getDrawable(R.drawable.ic_broken_image);
        holder.bindDrawable(placeholder);
        holder.bindRestaurant(restaurant);
        mIconDownloader.queueThumbnail(holder, restaurant.getIconURL());
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }
}