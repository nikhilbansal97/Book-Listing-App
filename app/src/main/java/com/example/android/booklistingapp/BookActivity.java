package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class BookActivity extends AppCompatActivity {
    public static final String LOG_TAG = BookActivity.class.getName();
    private EditText search;
    private String query_search_url;
    private ListView books_view;
    private BookAdapter adapter;
    private Button search_button;
    private ProgressBar loading;
    private TextView status;
    ConnectivityManager intmgr;
    NetworkInfo intinfo;
    String search_query;
    TextInputLayout search_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading = (ProgressBar) findViewById(R.id.loading);
        search_container = (TextInputLayout) findViewById(R.id.search_container);
        status = (TextView) findViewById(R.id.status);
        intmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        intinfo = intmgr.getActiveNetworkInfo();
        if (intinfo != null && intinfo.isConnected()) {
            status.setText(R.string.connected);
            status.setBackgroundColor(getResources().getColor(R.color.connected));
            status.setVisibility(View.GONE);
        } else {
            status.setText(R.string.not_connected);
            status.setBackgroundColor(getResources().getColor(R.color.not_connected));
            status.setVisibility(View.VISIBLE);
        }
        loading.setVisibility(View.INVISIBLE);
        books_view = (ListView) findViewById(R.id.books_list);
        search = (EditText) findViewById(R.id.search);
        search_button = (Button) findViewById(R.id.search_button);
        adapter = new BookAdapter(BookActivity.this, new ArrayList<Book>());
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                search_query = search.getText().toString();
                if (search_query.length() == 0) {
                    search_container.setErrorEnabled(true);
                    search_container.setError("Field cannot be empty!");
                } else {
                    search_container.setError("");
                    search_container.setErrorEnabled(false);
                    search_query = search_query.replaceAll(" ", "+");
                    query_search_url = "https://www.googleapis.com/books/v1/volumes?q=" + search_query + "&key=AIzaSyAxdKHeBJFK0FQakAibsCmblwp2KsEp72c";
                    books_view.setAdapter(adapter);
                    intmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    intinfo = intmgr.getActiveNetworkInfo();
                    if (intinfo != null && intinfo.isConnected()) {
                        status.setText(R.string.connected);
                        status.setBackgroundColor(getResources().getColor(R.color.connected));
                        status.setVisibility(View.GONE);
                        loading.setVisibility(View.VISIBLE);
                        adapter.clear();
                        Log.d(LOG_TAG, "Internet Connected");
                        Log.d(LOG_TAG, search_query);
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute(query_search_url);
                    } else {
                        status.setText(R.string.not_connected);
                        status.setBackgroundColor(getResources().getColor(R.color.not_connected));
                        status.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        books_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book current_book = adapter.getItem(position);
                Uri bookUri = Uri.parse(current_book.getUrl());
                Intent book_intent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(book_intent);
            }
        });
    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {


        @Override
        protected List<Book> doInBackground(String... urls) {
            Log.d(LOG_TAG, "doInBackground process started.....");
            if (urls.length < 1 || urls[0] == null)
                return null;
            List<Book> books_list = QueryUtils.getBooks(urls[0]);
            return books_list;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            Log.d(LOG_TAG, "onPostExecute process started.....");
            adapter.clear();
            if (books != null && !books.isEmpty()) {
                loading.setVisibility(View.INVISIBLE);
                adapter.addAll(books);
                Log.d(LOG_TAG, "Books added.....");
            }
        }
    }

}
