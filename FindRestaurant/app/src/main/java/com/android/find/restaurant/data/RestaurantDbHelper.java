package com.android.find.restaurant.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.find.restaurant.Restaurant;
import com.android.find.restaurant.data.RestaurantReaderContract.RestaurantEntry;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rishi on 3/22/16.
 */
public class RestaurantDbHelper extends SQLiteOpenHelper {

    public final static String TAG = "RestaurantDbHelper";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RestaurantReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RestaurantEntry.TABLE_NAME + " (" +
                    RestaurantEntry._ID + " INTEGER PRIMARY KEY," +
                    RestaurantEntry.COLUMN_NAME_RESTURANT_ID + TEXT_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_BUSINESS_NAME + TEXT_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_FAVOURITE + TEXT_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_ICON_URL + TEXT_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_LATLNG + TEXT_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_PHONE + TEXT_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_RATING + REAL_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_REVIEW_COUNTS + INTEGER_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_SNIPPET_IMG + TEXT_TYPE + COMMA_SEP +
                    RestaurantEntry.COLUMN_NAME_SNIPPET_TEXT + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RestaurantEntry.TABLE_NAME;

    private static RestaurantDbHelper sRestaurantDbHelper;

    public static RestaurantDbHelper getInstance(Context context){
        if(sRestaurantDbHelper == null){
            sRestaurantDbHelper = new RestaurantDbHelper(context);
        }
        return sRestaurantDbHelper;
    }

    private RestaurantDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "Deleting and creating table");
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Boolean updateFavoriteRestaurant(Restaurant restaurant){
        Log.d(TAG, "Verify and update favorite restaurant");
        if(isRestaurantExists(restaurant.getId())){
            updateFavoriteStatus(restaurant.getId(), restaurant.isIsFavorite());
        }else {
            insertFavoriteRestaurant(restaurant);
        }
        return true;
    }

    private void insertFavoriteRestaurant(Restaurant restaurant){
        Log.d(TAG, "Inserting values");
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RestaurantEntry.COLUMN_NAME_RESTURANT_ID, restaurant.getId());
        values.put(RestaurantEntry.COLUMN_NAME_BUSINESS_NAME, restaurant.getBusinessName());
        values.put(RestaurantEntry.COLUMN_NAME_ADDRESS, restaurant.getDisplayAddr());
        values.put(RestaurantEntry.COLUMN_NAME_FAVOURITE, restaurant.isIsFavorite());
        values.put(RestaurantEntry.COLUMN_NAME_ICON_URL, restaurant.getIconURL());
        values.put(RestaurantEntry.COLUMN_NAME_LATLNG, restaurant.getmLatLong());
        values.put(RestaurantEntry.COLUMN_NAME_PHONE, restaurant.getPhoneNumber());
        values.put(RestaurantEntry.COLUMN_NAME_RATING, restaurant.getRating());
        values.put(RestaurantEntry.COLUMN_NAME_REVIEW_COUNTS, restaurant.getReviewCounts());
        values.put(RestaurantEntry.COLUMN_NAME_SNIPPET_IMG, restaurant.getmSnippetImageURL());
        values.put(RestaurantEntry.COLUMN_NAME_SNIPPET_TEXT, restaurant.getSnippetText());

        long newRowId;
        newRowId = db.insert(
                RestaurantEntry.TABLE_NAME,
                null,
                values);

        db.close();
    }

    public List<Restaurant> fetchFavorites(){

        Log.d(TAG, "Fetching all the favorites");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ RestaurantEntry.TABLE_NAME, null);
        Log.d(TAG, "Total favorite entries " + cursor.getCount());

        List<Restaurant> restaurantList = new ArrayList<>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            Restaurant restaurant = new Restaurant();
            restaurant.setId(cursor.getString(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_RESTURANT_ID)));
            restaurant.setBusinessName(cursor.getString(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_BUSINESS_NAME)));
            restaurant.setDisplayAddr(cursor.getString(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_ADDRESS)));
            restaurant.setIsFavorite(getBoolean(cursor, RestaurantEntry.COLUMN_NAME_FAVOURITE));
            restaurant.setIconURL(cursor.getString(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_ICON_URL)));
            restaurant.setmLatLong(cursor.getString(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_LATLNG)));
            restaurant.setPhoneNumber(cursor.getString(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_PHONE)));
            restaurant.setRating(cursor.getFloat(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_RATING)));
            restaurant.setReviewCounts(cursor.getInt(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_REVIEW_COUNTS)));
            restaurant.setmSnippetImageURL(cursor.getString(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_SNIPPET_IMG)));
            restaurant.setSnippetText(cursor.getString(cursor.getColumnIndex(RestaurantEntry.COLUMN_NAME_SNIPPET_TEXT)));

            restaurantList.add(restaurant);
        }
        cursor.close();
        return restaurantList;
    }

    private boolean getBoolean(Cursor cursor, String columnName){
        if(cursor.getInt(cursor.getColumnIndex(columnName)) == 0){
            return false;
        }
        return true;
    }

    public boolean isRestaurantExists(String restaurantId){
        SQLiteDatabase db = getReadableDatabase();

        Log.d(TAG, "Checking if already exists");
        String [] projections = {RestaurantEntry.COLUMN_NAME_RESTURANT_ID};
        String whereClause = RestaurantEntry.COLUMN_NAME_RESTURANT_ID + "=?";
        String [] whereArgs = {restaurantId};

        Cursor cursor = db.query(RestaurantEntry.TABLE_NAME, projections, whereClause, whereArgs, null, null, null);
        //Cursor cursor = db.rawQuery("SELECT * FROM "+ RestaurantEntry.TABLE_NAME + " WHERE restaurant_id =" + restaurantId, null);

        cursor.moveToFirst();
        if(cursor.getCount() == 0){
            return false;
        }
        cursor.close();
        return true;
    }

    private void updateFavoriteStatus(String restaurantId, boolean isFavorite){
        SQLiteDatabase db = getReadableDatabase();

        Log.d(TAG, "Running update");
        ContentValues values = new ContentValues();
        values.put(RestaurantEntry.COLUMN_NAME_FAVOURITE, isFavorite);

        String whereClause = RestaurantEntry.COLUMN_NAME_RESTURANT_ID + "=?";
        String [] whereArgs = {restaurantId};

        db.update(RestaurantEntry.TABLE_NAME, values, whereClause, whereArgs);
    }
}
