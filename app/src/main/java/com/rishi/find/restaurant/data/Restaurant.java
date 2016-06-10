package com.rishi.find.restaurant.data;

/**
 * Holds the restaurant data.
 * Created by rishi on 3/17/16.
 */
public class Restaurant {

    private String mId;
    private String mBusinessName;
    private String mDisplayAddress;
    private float mRating;
    private String mIconURL;
    private int mReviewCounts;
    private String mSnippetText;
    private boolean mIsFavorite;
    private String mPhoneNumber;
    private String mLatLong;
    private String mSnippetImageURL;


    public Restaurant(){
        mIsFavorite = false;
    }

    public String getBusinessName() {
        return mBusinessName;
    }

    public void setBusinessName(String mBusinessName) {
        this.mBusinessName = mBusinessName;
    }

    public String getDisplayAddress() {
        return mDisplayAddress;
    }

    public void setDisplayAddress(String mDisplayAddr) {
        this.mDisplayAddress = mDisplayAddr;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float mRating) {
        this.mRating = mRating;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id){
        this.mId = id;
    }

    public String getIconURL() {
        return mIconURL;
    }

    public void setIconURL(String mIconURL) {
        this.mIconURL = mIconURL;
    }

    public int getReviewCounts() {
        return mReviewCounts;
    }

    public void setReviewCounts(int mReviewCounts) {
        this.mReviewCounts = mReviewCounts;
    }

    public String getSnippetText() {
        return mSnippetText;
    }

    public void setSnippetText(String mSnippetText) {
        this.mSnippetText = mSnippetText;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean mIsFavorite) {
        this.mIsFavorite = mIsFavorite;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getLatLong() {
        return mLatLong;
    }

    public void setLatLong(String mLatLong) {
        this.mLatLong = mLatLong;
    }

    public String getSnippetImageURL() {
        return mSnippetImageURL;
    }

    public void setSnippetImageURL(String mSnippetImageURL) {
        this.mSnippetImageURL = mSnippetImageURL;
    }
}
