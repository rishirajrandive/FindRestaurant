package com.android.find.restaurant;

import android.util.Log;

/**
 * Created by rishi on 3/17/16.
 */
public class Restaurant {

    private String mId;
    private String mBusinessName;
    private String mDisplayAddr;
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

    public String getDisplayAddr() {
        return mDisplayAddr;
    }

    public void setDisplayAddr(String mDisplayAddr) {
        this.mDisplayAddr = mDisplayAddr;
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

    public boolean isIsFavorite() {
        return mIsFavorite;
    }

    public void setIsFavorite(boolean mIsFavorite) {
        this.mIsFavorite = mIsFavorite;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmLatLong() {
        return mLatLong;
    }

    public void setmLatLong(String mLatLong) {
        this.mLatLong = mLatLong;
    }

    public String getmSnippetImageURL() {
        return mSnippetImageURL;
    }

    public void setmSnippetImageURL(String mSnippetImageURL) {
        this.mSnippetImageURL = mSnippetImageURL;
    }
}
