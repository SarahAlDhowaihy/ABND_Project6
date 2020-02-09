package com.example.android.booklisting;

/**
 * Created by sarahaldowihy on 9/11/2017 AD.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving data from google book.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            if (baseJsonResponse.getInt("totalItems") == 0) {
                return books;
            }

            JSONArray bookJsonArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < bookJsonArray.length(); i++) {
                JSONObject itemJsonObject = bookJsonArray.getJSONObject(i);
                JSONObject bookInfo = itemJsonObject.getJSONObject("volumeInfo");
                String bookName = bookInfo.getString("title");
                /**
                 * GET  Author Name from JSONArray BY method getAuthorName
                 */
                JSONArray authorsArray = bookInfo.getJSONArray("authors");
                String authorName = getAuthorName(authorsArray);

                // Create a new {@link Book} object with the book name and author
                // from the JSON response.
                Book book = new Book(bookName, authorName);
                // Add the new {@link Book} to the list of books.
                books.add(book);

            } //end for loop

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Book JSON results", e);
        }

        // Return the list of Books
        return books;
    }

    public static String getAuthorName(JSONArray authorsArray) throws JSONException {
        String authorName = null;
        if (authorsArray.length() == 0) {
            return authorName;
        }
        for (int i = 0; i < authorsArray.length(); i++) {
            if (i == 0) {
                authorName = authorsArray.getString(0);
            } else { //if it has multi author
                authorName += ", " + authorsArray.getString(i);
            }
        }
        return authorName;
    }
}