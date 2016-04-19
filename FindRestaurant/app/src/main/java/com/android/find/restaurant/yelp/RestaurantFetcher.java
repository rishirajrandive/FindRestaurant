package com.android.find.restaurant.yelp;

/**
 * Created by rishi on 3/20/16.
 */

import android.content.Context;
import android.util.Log;

import com.android.find.restaurant.Restaurant;
import com.android.find.restaurant.data.RestaurantDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;

/**
 * Example for accessing the RestaurantFetcher API.
 */
public class RestaurantFetcher {

    private final static String CONSUMER_KEY = "pGnuIvhI3ixeTWsV2blqnA";
    private final static String CONSUMER_SECRET = "w09DiKBYfTrK2d2UgjEM8LInskA";
    private final static String TOKEN = "h0gN-v7xcDe6ud8VEGlx-OTWFWGcOb06";
    private final static String TOKEN_SECRET = "XX156LyiZC-mwJjHbIMIW8TSZuA";

    private int mTotalResults = 20;
    private OAuthService mService;
    private Token mAccessToken;
    private List<Restaurant> mRestaurantList;

    private static RestaurantFetcher sRestaurantFetcher;

    public final static String TAG = "RestaurantFetcher";

    public static RestaurantFetcher get(Context context) {
        if (sRestaurantFetcher == null) {
            sRestaurantFetcher = new RestaurantFetcher(context);
        }
        return sRestaurantFetcher;
    }

    private RestaurantFetcher(Context context){
        this.mService = new ServiceBuilder().provider(YelpApi2.class).apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build();
        this.mAccessToken = new Token(TOKEN, TOKEN_SECRET);
    }

    /**
     * Search with term and location.
     *
     * @param term Search term
     * @param latitude Latitude
     * @param longitude Longitude
     * @return JSON string response
     */
    public List<Restaurant> search(String term, double latitude, double longitude, int sort) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        request.addQuerystringParameter("sort", sort+"");
        request.addQuerystringParameter("radius_filter", "16093");
        request.addQuerystringParameter("category_filter", "restaurants");
        this.mService.signRequest(this.mAccessToken, request);
        Response response = request.send();

        mRestaurantList = new ArrayList<>();
        try {
            String jsonString = response.getBody();
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(mRestaurantList, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return mRestaurantList;
    }

    private void parseItems(List<Restaurant> restaurantList, JSONObject jsonObject) throws JSONException{
        JSONArray businessesArray = jsonObject.getJSONArray("businesses");

        mTotalResults = (businessesArray.length() > mTotalResults ? mTotalResults : businessesArray.length());

        for (int i = 0; i < mTotalResults; i++) {
            JSONObject businessJsonObject = businessesArray.getJSONObject(i);

            Restaurant item = new Restaurant();
            item.setId(businessJsonObject.getString("id"));
            item.setBusinessName(businessJsonObject.getString("name"));
            item.setRating(businessJsonObject.getInt("rating"));
            item.setIconURL(businessJsonObject.getString("image_url"));
            item.setReviewCounts(businessJsonObject.getInt("review_count"));
            item.setmSnippetImageURL(businessJsonObject.getString("snippet_image_url"));
            item.setSnippetText(businessJsonObject.getString("snippet_text"));

            if(businessJsonObject.has("phone")){
                item.setPhoneNumber(businessJsonObject.getString("phone"));
            }
            JSONObject locationObject = businessJsonObject.getJSONObject("location");
            JSONArray displayAddrArray = locationObject.getJSONArray("display_address");

            String address = "";
            for(int j=0; j<displayAddrArray.length(); j++){
                address = address + displayAddrArray.get(j);
            }

            JSONObject coordinateObject = locationObject.getJSONObject("coordinate");
            item.setmLatLong(coordinateObject.getString("latitude") + "," + coordinateObject.getString("longitude"));

            item.setDisplayAddr(address);

            restaurantList.add(item);
        }
    }

    public List<Restaurant> getAllRestaurants(){
        return mRestaurantList;
    }

    public Restaurant getRestaurant(String id){
        for(Restaurant restaurant : mRestaurantList){
            if(restaurant.getId().equalsIgnoreCase(id)){
                return restaurant;
            }
        }
        return null;
    }

    public List<Restaurant> fetchFavorites(Context context){
        mRestaurantList = RestaurantDbHelper.getInstance(context).fetchFavorites();
        return mRestaurantList;
    }
}
