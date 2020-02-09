package com.example.android.booklisting;

/**
 * Created by sarahaldowihy on 9/11/2017 AD.
 */

public class Book {
    private String mBook;
    private String mAuthor;

    public Book(String mBook, String mAuthor) {
        this.mBook = mBook;
        this.mAuthor = mAuthor;
    }

    public String getBook() {
        return mBook;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
