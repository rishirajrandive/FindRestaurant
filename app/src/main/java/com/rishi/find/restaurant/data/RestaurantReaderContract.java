package com.rishi.find.restaurant.data;

import android.provider.BaseColumns;

/**
 * Contract for Favourite restaurants database
 * Created by rishi on 3/22/16.
 */
public class RestaurantReaderContract {
    public RestaurantReaderContract(){}

    public static abstract class RestaurantEntry implements BaseColumns {
        public static final String TABLE_NAME = "restaurant";
        public static final String COLUMN_NAME_RESTURANT_ID = "restaurant_id";
        public static final String COLUMN_NAME_BUSINESS_NAME = "business_name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_ICON_URL = "icon_url";
        public static final String COLUMN_NAME_REVIEW_COUNTS = "review_counts";
        public static final String COLUMN_NAME_SNIPPET_TEXT = "snippet_text";
        public static final String COLUMN_NAME_FAVOURITE = "favourite";
        public static final String COLUMN_NAME_PHONE = "phone_number";
        public static final String COLUMN_NAME_LATLNG = "latitude_longitude";
        public static final String COLUMN_NAME_SNIPPET_IMG = "snippet_image_url";
    }
}
