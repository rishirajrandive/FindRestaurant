package com.android.find.restaurant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.find.restaurant.data.RestaurantDbHelper;
import com.android.find.restaurant.yelp.RestaurantFetcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rishi on 3/21/16.
 */
public class RestaurantFragment extends Fragment {

    private static final String TAG = "RestaurantFragment";
    private static final String ARG_RESTAURANT_ID = "restaurant_id";
    private static final String GOOGLE_STATIC_MAP_BASE_URL = "http://maps.googleapis.com/maps/api/staticmap?" +
            "zoom=13&scale=2&size=600x400&maptype=roadmap&key=AIzaSyCdQc01dvpmsCnHzGAM9IR4wDhr6IhpMbo&" +
            "format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:%7C";

    private Restaurant mRestaurant;
    private TextView mRestaurantName;
    private TextView mTotalReviews;
    private RatingBar mRatings;
    private TextView mPhoneNumber;
    private ImageView mStaticMap;
    private TextView mAddress;
    private ImageView mSnippetImage;
    private TextView mSnippetText;
    private ToggleButton mFavToggleButton;

    public static RestaurantFragment newInstance(String restaurantId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESTAURANT_ID, restaurantId);

        RestaurantFragment fragment = new RestaurantFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        String restaurantId = (String) getArguments().getSerializable(ARG_RESTAURANT_ID);
        mRestaurant = RestaurantFetcher.get(getActivity()).getRestaurant(restaurantId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setIcon(R.);

        mRestaurantName = (TextView) v.findViewById(R.id.restaurant_name);
        mRestaurantName.setText(mRestaurant.getBusinessName());

        mTotalReviews = (TextView) v.findViewById(R.id.total_reviews);
        mTotalReviews.setText(mRestaurant.getReviewCounts() + " Reviews");

        mRatings = (RatingBar) v.findViewById(R.id.restaurant_rating);
        mRatings.setRating(mRestaurant.getRating());

        mPhoneNumber = (TextView) v.findViewById(R.id.phone_number);
        mPhoneNumber.setText(mRestaurant.getPhoneNumber());

        mAddress = (TextView) v.findViewById(R.id.restaurant_address);
        mAddress.setText(mRestaurant.getDisplayAddr());

        mSnippetText = (TextView) v.findViewById(R.id.snippet_text);
        mSnippetText.setText(mRestaurant.getSnippetText());

        mStaticMap = (ImageView) v.findViewById(R.id.static_map);
        mSnippetImage = (ImageView) v.findViewById(R.id.snippet_image);

        mFavToggleButton = (ToggleButton) v.findViewById(R.id.fav_toggle_button);
        new CheckFavorite().execute();
        mFavToggleButton.setOnClickListener(new FavouriteClickListener());

        String staticMapURL = GOOGLE_STATIC_MAP_BASE_URL + mRestaurant.getmLatLong() + "&center=" + mRestaurant.getmLatLong();
        new FetchImages(staticMapURL, mStaticMap).execute();

        new FetchImages(mRestaurant.getmSnippetImageURL(), mSnippetImage).execute();

        return v;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                if(NavUtils.getParentActivityIntent(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class FetchImages extends AsyncTask<Void, Void, Bitmap>{

        private String mStaticMapURL;
        private ImageView mImageView;

        public FetchImages(String url, ImageView imageView){
            mStaticMapURL = url;
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bmp = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(mStaticMapURL);
                connection = (HttpURLConnection)url.openConnection();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = connection.getInputStream();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException(connection.getResponseMessage() +
                            ": with " +
                            mStaticMapURL);
                }
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                bmp = BitmapFactory
                        .decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);
            } catch (Exception ex) {

            }finally {
                connection.disconnect();
            }
            return bmp;
        }
        @Override
        protected void onPostExecute(Bitmap bmp) {
            if (bmp!=null) {
                mImageView.setImageBitmap(bmp);
            }
        }
    }

    private class FavouriteClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            AsyncTask<Void, Void, Boolean> updateFavoriteRestaurantTask = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    return RestaurantDbHelper.getInstance(getContext()).updateFavoriteRestaurant(mRestaurant);
                }
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    Log.d(TAG, "Favorite updated in database");
                }
            }.execute();
        }
    }

    private class CheckFavorite extends AsyncTask<Void, Void, Boolean>{

        private boolean isFavorite;

        @Override
        protected Boolean doInBackground(Void... params) {
            isFavorite = RestaurantDbHelper.getInstance(getContext()).isRestaurantExists(mRestaurant.getId());
            return isFavorite;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mRestaurant.setIsFavorite(isFavorite);
            mFavToggleButton.setChecked(isFavorite);
        }


    }
}