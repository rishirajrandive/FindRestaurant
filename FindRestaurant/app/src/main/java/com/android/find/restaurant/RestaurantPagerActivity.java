package com.android.find.restaurant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.find.restaurant.yelp.RestaurantFetcher;

import java.util.List;

public class RestaurantPagerActivity extends AppCompatActivity {

    private static final String EXTRA_RESTAURANT_ID =
            "com.android.find.restaurant.restaurant_id";

    private ViewPager mViewPager;
    private List<Restaurant> mRestaurantList;

    public static Intent newIntent(Context packageContext, String restaurantId) {
        Intent intent = new Intent(packageContext, RestaurantPagerActivity.class);
        intent.putExtra(EXTRA_RESTAURANT_ID, restaurantId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_pager);

        String restaurantId = (String) getIntent()
                .getSerializableExtra(EXTRA_RESTAURANT_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_restaurant_pager);

        mRestaurantList = RestaurantFetcher.get(this).getAllRestaurants();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Restaurant restaurant = mRestaurantList.get(position);
                return RestaurantFragment.newInstance(restaurant.getId());
            }

            @Override
            public int getCount() {
                return mRestaurantList.size();
            }
        });

        for (int i = 0; i < mRestaurantList.size(); i++) {
            if (mRestaurantList.get(i).getId().equalsIgnoreCase(restaurantId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}