package com.example.android.booklistingapp;


/**
 * Created by NIKHIL on 11-02-2017.
 */

public class Book {

    private String mName;
    private String mAuthor;
    private String mImageResourceUrl;
    private String mUrl;

    public Book(String name, String author, String imageResourceUrl, String url) {
        mName = name;
        mAuthor = author;
        mImageResourceUrl = imageResourceUrl;
        mUrl = url;
    }

    public String getName() {
        return mName;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getImageResource() {
        return mImageResourceUrl;
    }

    public String getUrl() {
        return mUrl;
    }

}
