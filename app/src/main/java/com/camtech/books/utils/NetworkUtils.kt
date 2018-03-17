package com.camtech.books.utils

import android.net.Uri
import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

object NetworkUtils {

    private val TAG = NetworkUtils::class.java.simpleName
    private val BASE_URL = "https://www.googleapis.com/books/v1/volumes"
    private var connectionTimeoutListener: ConnectionListener? = null

    fun setOnConnectionTimeoutListener(connectionTimeoutListener: ConnectionListener) {
        this.connectionTimeoutListener = connectionTimeoutListener
    }

    fun buildUrl(search: String, maxResults: Int, startIndex: Int): URL? {
        val builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("q", search.trim())
                .appendQueryParameter("maxResults", maxResults.toString())
                .appendQueryParameter("startIndex", startIndex.toString())
                .build()
        var url: URL? = null
        try {
            url = URL(builtUri.toString())
            Log.i(TAG, "Built URL: " + url.toString())
        } catch (e: MalformedURLException) {
            Log.e(TAG, "Error building URL", e)
        }

        return url
    }

    @Throws(IOException::class)
    fun makeHttpRequest(url: URL?): String {
        var jsonResponse = ""

        // If the URL is null or empty, return early.
        if (url == null || url.toString().isEmpty()) {
            return jsonResponse
        }

        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            urlConnection.connect()
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.responseCode)
            }
        } catch (e: IOException) {
            connectionTimeoutListener?.onConnectionTimeout(e)
            Log.e(TAG, "Problem retrieving JSON results.", e)
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
        }
        return jsonResponse
    }

    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line: String? = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }
}
