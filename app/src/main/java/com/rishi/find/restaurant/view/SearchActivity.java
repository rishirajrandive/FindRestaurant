package com.rishi.find.restaurant.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.rishi.find.restaurant.AbstractFragmentActivity;
import com.rishi.find.restaurant.R;

/**
 * Default activity for the application, initiates shows default search.
 * Created by rishi on 3/17/16.
 */
public class SearchActivity extends AbstractFragmentActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SearchActivity";
    public static final String SEARCH_FRAGMENT_TAG = "SearchFragment";
    public static final String FAVORITE_FRAGMENT_TAG = "FavoriteFragment";

    private int mNavItemId;

    @Override
    protected Fragment createFragment() {
        return new SearchFragment();
    }

    @Override
    public void updateFragment(Fragment fragment, String fragmentTag) {
        super.updateFragment(fragment, fragmentTag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.search);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mNavItemId = item.getItemId();
        updateView();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Sets default view with @SearchFragment
     * @param selectedFragment
     */
//    private void setDefaultView(Fragment selectedFragment){
//        Log.d(TAG, "Setting default view " + getLayoutResId());
//        setContentView(getLayoutResId());
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.restaurant_list_fragment);
//
//        if (fragment == null) {
//            fragment = selectedFragment;
//            fm.beginTransaction()
//                    .add(R.id.restaurant_list_fragment, fragment)
//                    .commit();
//        }
//    }

    /**
     * Updates view based on the navigation drawer choice, it is either @SearchFragment or @FavoriteFragment
     */
    private void updateView(){
        Log.d(TAG, "View updating "+ mNavItemId);
        if(mNavItemId == R.id.search_screen){
            getSupportActionBar().setTitle(R.string.search);
            updateFragment(new SearchFragment(), SEARCH_FRAGMENT_TAG);

        }else if(mNavItemId == R.id.fav_screen){
            getSupportActionBar().setTitle(R.string.favorite);
            updateFragment(new FavoriteFragment(), FAVORITE_FRAGMENT_TAG);
        }
    }

    /**
     * Returns the relevant resource ID for view based on tablet or phone.
     * @return
     */
    private int getLayoutResId(){
        if(isTabletAndLandscape()){
            Log.d(TAG, "Tablet layout returned");
            return R.layout.activity_merge_tablet;
        }
        Log.d(TAG, "Normal layout returned");
        return R.layout.activity_search_list;
    }

    /**
     * Returns if the device is tablet
     * @return
     */
    private boolean isTabletAndLandscape(){
        Configuration config = getApplicationContext().getResources().getConfiguration();
        if(config.smallestScreenWidthDp >= 400){
            return true;
        }
        return false;
    }
}
