package com.rishi.find.restaurant.view.helper;

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

import com.rishi.find.restaurant.R;
import com.rishi.find.restaurant.data.Restaurant;
import com.rishi.find.restaurant.view.DetailFragment;
import com.rishi.find.restaurant.view.DetailPagerActivity;

/**
 * View Holder for restaurants list
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

    /**
     * Holder object created with required parameters
     * @param itemView
     * @param context
     * @param fragmentManager
     */
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

    /**
     * Bind the image of restaurant
     * @param drawable
     */
    public void bindDrawable(Drawable drawable) {
        mRestaurantImageView.setImageDrawable(drawable);
    }

    /**
     * Bind restaurant
     * @param aRestaurant
     */
    public void bindRestaurant(Restaurant aRestaurant) {
        mRestaurant = aRestaurant;
        mNameTextView.setText(mRestaurant.getBusinessName());
        mAddressTextView.setText(mRestaurant.getDisplayAddress());
        mRatingBar.setRating(mRestaurant.getRating());
    }

    @Override
    public void onClick(View v) {
        if (mActivity.findViewById(R.id.detail_fragment) == null) {
            Intent intent = DetailPagerActivity.newIntent(mActivity, mRestaurant.getId());
            mActivity.startActivity(intent);
        } else {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            Fragment oldFragment = mFragmentManager.findFragmentById(R.id.detail_fragment);
            Fragment newDetail = DetailFragment.newInstance(mRestaurant.getId());

            if (oldFragment != null) {
                fragmentTransaction.remove(oldFragment);
            }

            fragmentTransaction.add(R.id.detail_fragment, newDetail);
            fragmentTransaction.commit();
        }
    }
}