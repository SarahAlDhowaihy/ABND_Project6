package com.example.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sarahaldowihy on 9/11/2017 AD.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.book_list_item, parent, false);
        }
        // Get the data item for this position
        Book currentBook = getItem(position);
        // Name of book
        TextView bookName = (TextView) listItemView.findViewById(R.id.bookName);
        bookName.setText(currentBook.getBook());
        // Name of author
        TextView authorName = (TextView) listItemView.findViewById(R.id.authorName);
        authorName.setText(currentBook.getAuthor());
        return listItemView;
    }
}
