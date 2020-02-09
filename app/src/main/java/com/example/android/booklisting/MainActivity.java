package com.example.android.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * Search Bar
     */
    private EditText searchBooks;
    private Button searchButton;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    /**
     * Book Adapter
     */
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);
        // Create a new adapter that takes an empty list of books as input
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(bookAdapter);
        /** Empty TextView */
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        /** Search Bar */
        searchBooks = (EditText) findViewById(R.id.searchBooks);
        searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute();
                } else {
                    /** If there no Internet connection show the message **/
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }

            }
        });
    }

    private void updateUi(List<Book> books) {
        if (books.isEmpty()) {
            // If no books found, show a message
            mEmptyStateTextView.setText(R.string.no_data_found);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
        }
        bookAdapter.clear();
        bookAdapter.addAll(books);
        Log.i("BookAdapter", "done Add to All ");
    }

    private String getUrlForHttpRequest() {
        final String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=search+";
        String searchQuery = searchBooks.getText().toString().replaceAll(" ", "+");
        String url = baseUrl + searchQuery;
        return url;
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, List<Book>> {
        @Override
        protected List<Book> doInBackground(URL... urls) {
            URL url = createURL(getUrlForHttpRequest());
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Book> books = parseJson(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books == null) {
                return;
            }
            updateUi(books);
        }

        private URL createURL(String stringUrl) {
            try {
                return new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("mainActivity", "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<Book> parseJson(String json) {
            if (json == null) {
                return null;
            }
            List<Book> books = QueryUtils.extractFeatureFromJson(json);
            return books;
        }
    }
}
