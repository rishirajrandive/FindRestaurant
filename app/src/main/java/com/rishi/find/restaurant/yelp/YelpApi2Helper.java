package com.rishi.find.restaurant.yelp;

/**
 * Created by rishi on 3/20/16.
 */
import org.scribe.model.Token;
import org.scribe.builder.api.DefaultApi10a;

/**
 * Service provider for "2-legged" OAuth10a for RestaurantFetcher API (version 2).
 */
public class YelpApi2Helper extends DefaultApi10a {

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(Token arg0) {
        return null;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return null;
    }

}