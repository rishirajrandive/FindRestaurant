package com.rishi.find.restaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.rishi.find.restaurant.view.SearchActivity;

/**
 * Abstract class to add fragments to the containers in the activity
 * Created by rishi on 5/1/16.
 */
public abstract class AbstractFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.restaurant_list_fragment);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.restaurant_list_fragment, fragment, SearchActivity.SEARCH_FRAGMENT_TAG)
                    .commit();
        }
    }
    public void updateFragment(Fragment fragment, String fragmentTag){
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
//            if(fragmentManager.findFragmentByTag(fragmentTag) == null){
//                fragmentManager.beginTransaction()
//                        .replace(R.id.restaurant_list_fragment, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
//            }else {
                fragmentManager.beginTransaction()
                        .replace(R.id.restaurant_list_fragment, fragment, fragmentTag).commit();
            //}
        }
    }
}
