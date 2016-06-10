package com.rishi.find.restaurant.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.rishi.find.restaurant.R;
import com.rishi.find.restaurant.data.Restaurant;
import com.rishi.find.restaurant.yelp.RestaurantFetcher;

import java.util.List;
import java.util.Stack;
/**
 * Handles ViewPager view for the application.
 * Created by rishi on 3/17/16.
 */
public class DetailPagerActivity extends AppCompatActivity {

    public static final String TAG = "RestaurantPagerActity";
    private static final String EXTRA_RESTAURANT_ID =
            "com.android.find.restaurant.restaurant_id";

    private ViewPager mViewPager;
    private List<Restaurant> mRestaurantList;
    private Stack<Integer> mPageStack = new Stack<>();

    /**
     * Gives new intent to show the details
     * @param packageContext
     * @param restaurantId
     * @return
     */
    public static Intent newIntent(Context packageContext, String restaurantId) {
        Intent intent = new Intent(packageContext, DetailPagerActivity.class);
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

        mRestaurantList = RestaurantFetcher.getInstance(this).getAllRestaurants();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Restaurant restaurant = mRestaurantList.get(position);
                return DetailFragment.newInstance(restaurant.getId());
            }

            @Override
            public int getCount() {
                return mRestaurantList.size();
            }
        });

        for (int i = 0; i < mRestaurantList.size(); i++) {
            if (mRestaurantList.get(i).getId().equalsIgnoreCase(restaurantId)) {
                mViewPager.setCurrentItem(i);
                mPageStack.push(i);
                break;
            }
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mPageStack.empty()) {
                    mPageStack.push(0);
                }
                if (mPageStack.contains(position)) {
                    mPageStack.remove(mPageStack.indexOf(position));
                    mPageStack.push(position);
                } else {
                    mPageStack.push(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mPageStack.size() > 1){
            mPageStack.pop();
            mViewPager.setCurrentItem(mPageStack.lastElement());
        }else if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}