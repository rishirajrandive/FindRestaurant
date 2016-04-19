package com.android.find.restaurant.view.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.find.restaurant.R;
import com.android.find.restaurant.Restaurant;
import com.android.find.restaurant.RestaurantFragment;
import com.android.find.restaurant.RestaurantPagerActivity;

/**
 * Created by rishi on 3/23/16.
 */
public class RestaurantHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private ImageView mRestaurantImageView;

    private TextView mNameTextView;

    private TextView mAddressTextView;

    private RatingBar mRatingBar;

    private Restaurant mRestaurant;

    private Activity mActivity;

    private FragmentManager mFragmentManager;

    public RestaurantHolder(View itemView, Activity context, FragmentManager fragmentManager) {
        super(itemView);
        itemView.setOnClickListener(this);

        mActivity = context;
        mFragmentManager = fragmentManager;
        mRestaurantImageView = (ImageView) itemView.findViewById(R.id.restaurant_imageview);
        mNameTextView = (TextView) itemView.findViewById(R.id.restaurant_name);
        mAddressTextView = (TextView) itemView.findViewById(R.id.restaurant_address);
        mRatingBar = (RatingBar) itemView.findViewById(R.id.restaurant_rating);
    }

    public void bindDrawable(Drawable drawable) {
        mRestaurantImageView.setImageDrawable(drawable);
    }

    public void bindRestaurant(Restaurant aRestaurant) {
        mRestaurant = aRestaurant;
        mNameTextView.setText(mRestaurant.getBusinessName());
        mAddressTextView.setText(mRestaurant.getDisplayAddr());
        mRatingBar.setRating(mRestaurant.getRating());
    }

    @Override
    public void onClick(View v) {
        //Intent intent = RestaurantPagerActivity.newIntent(mActivity, mRestaurant.getId());
        //mActivity.startActivity(intent);

        if (mActivity.findViewById(R.id.detailFragmentContainer) == null) {
            // start an instance of CrimePagerActivity
            Intent intent = RestaurantPagerActivity.newIntent(mActivity, mRestaurant.getId());
            mActivity.startActivity(intent);
        } else {
            FragmentTransaction ft = mFragmentManager.beginTransaction();

            Fragment oldDetail = mFragmentManager.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = RestaurantFragment.newInstance(mRestaurant.getId());

            if (oldDetail != null) {
                ft.remove(oldDetail);
            }

            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }
    }


}