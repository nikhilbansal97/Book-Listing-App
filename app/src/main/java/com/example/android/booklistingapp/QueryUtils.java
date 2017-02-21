package com.example.android.booklistingapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import static com.example.android.booklistingapp.BookActivity.LOG_TAG;

/**
 * Created by NIKHIL on 11-02-2017.
 */

public final class QueryUtils {

    private QueryUtils() {
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
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

    private static String readStream(InputStream inputStream) throws IOException {
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

    public static List<Book> extractJsonFeatures(String bookJson) {

        if (bookJson.isEmpty())
            return null;

        List<Book> books_list = new ArrayList<>();
        try {
            JSONObject book_object = new JSONObject(bookJson);
            JSONArray items = book_object.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject items_object = items.getJSONObject(i);
                JSONObject volumeInfo = items_object.getJSONObject("volumeInfo");
                String previewLink = volumeInfo.getString("previewLink");
                String name = volumeInfo.getString("title");
                JSONArray authors;
                String author = "";
                JSONObject imageLinks;
                String imageUrl = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcR2Mlfd3Qo223fIgVdA1Fg9psRSNkZqmErDInIGz6c7yCcrwV5WYA";
                if (volumeInfo.has("imageLinks")) {
                    imageLinks = volumeInfo.getJSONObject("imageLinks");
                    imageUrl = imageLinks.getString("smallThumbnail");
                }
                if (volumeInfo.has("authors")) {
                    authors = volumeInfo.getJSONArray("authors");
                    author = authors.getString(0);
                }
                books_list.add(new Book(name, author, imageUrl, previewLink));
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return books_list;
    }

    public static List<Book> getBooks(String requestUrl) {
        URL url = createUrl(requestUrl);
        String response = null;
        try {
            response = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request", e);
        }
        List<Book> books_list = extractJsonFeatures(response);
        return books_list;
    }

    public static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}
