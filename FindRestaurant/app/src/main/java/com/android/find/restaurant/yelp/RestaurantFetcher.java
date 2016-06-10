package com.android.find.restaurant.yelp;

/**
 * Created by rishi on 3/20/16.
 */

import android.content.Context;
import android.util.Log;

import com.android.find.restaurant.data.Restaurant;
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
 * Fetches all the restaurants for new search or favourite search.
 * Yelp API is used for fetching the details for restaurant for search.
 * Some search parameters are hard coded such as radius filter and category filter.
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

    /**
     * Static method to return the instance if it is not created, as this is a singleton class.
     * @param context
     * @return
     */
    public static RestaurantFetcher getInstance(Context context) {
        if (sRestaurantFetcher == null) {
            sRestaurantFetcher = new RestaurantFetcher(context);
        }
        return sRestaurantFetcher;
    }

    /**
     * Internal private constructor invoked once.
     * @param context
     */
    private RestaurantFetcher(Context context){
        this.mService = new ServiceBuilder().provider(YelpApi2Helper.class).apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build();
        this.mAccessToken = new Token(TOKEN, TOKEN_SECRET);
    }

    /**
     * Search with term, location and sort by option and throws back the exception.
     * @param term
     * @param latitude
     * @param longitude
     * @param sort
     * @return
     */
    public List<Restaurant> search(String term, double latitude, double longitude, int sort) throws JSONException{
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        request.addQuerystringParameter("sort", sort+"");
        request.addQuerystringParameter("radius_filter", "16093");
        request.addQuerystringParameter("category_filter", "restaurants");
        this.mService.signRequest(this.mAccessToken, request);
        Response response = request.send();

        mRestaurantList = new ArrayList<>();
        String jsonString = response.getBody();
        JSONObject jsonBody = new JSONObject(jsonString);
        parseItems(mRestaurantList, jsonBody);
        return mRestaurantList;
    }

    /**
     * Parse JSON received from the Yelp search.
     * @param restaurantList
     * @param jsonObject
     * @throws JSONException
     */
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
            item.setSnippetImageURL(businessJsonObject.getString("snippet_image_url"));
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
            item.setLatLong(coordinateObject.getString("latitude") + "," + coordinateObject.getString("longitude"));

            item.setDisplayAddress(address);

            restaurantList.add(item);
        }
    }

    /**
     * Method to return all the restaurants.
     * @return
     */
    public List<Restaurant> getAllRestaurants(){
        return mRestaurantList;
    }

    /**
     * Get a specific restaurant data.
     * @param id
     * @return
     */
    public Restaurant getRestaurant(String id){
        for(Restaurant restaurant : mRestaurantList){
            if(restaurant.getId().equalsIgnoreCase(id)){
                return restaurant;
            }
        }
        return null;
    }

    /**
     * Fetches favourites data from local database for favourite screen.
     * @param context
     * @return
     */
    public List<Restaurant> fetchFavorites(Context context){
        mRestaurantList = RestaurantDbHelper.getInstance(context).fetchFavorites();
        return mRestaurantList;
    }
}
