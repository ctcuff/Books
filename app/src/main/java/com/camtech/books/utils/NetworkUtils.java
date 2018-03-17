package com.camtech.books.utils;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private final static String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static ConnectionListener connectionTimeoutListener;

    public static URL buildUrl(String search, int maxResults, int startIndex) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("q", search.trim())
                .appendQueryParameter("maxResults", String.valueOf(maxResults))
                .appendQueryParameter("startIndex", String.valueOf(startIndex))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.i(TAG, "Built URL: " + url.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error building URL", e);
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null or empty, return early.
        if (url == null || url.toString().isEmpty()) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            if (connectionTimeoutListener != null)
                connectionTimeoutListener.onConnectionTimeout(e);
            Log.e(TAG, "Problem retrieving JSON results.", e);
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

    private static String readFromStream(InputStream inputStream) throws IOException {
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

    public static void setOnConnectionTimeoutListener(ConnectionListener connectionTimeoutListener) {
        NetworkUtils.connectionTimeoutListener = connectionTimeoutListener;
    }
}
