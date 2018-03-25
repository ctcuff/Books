package com.camtech.books.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.camtech.books.database.DBContract
import com.camtech.books.database.DBHelper
import com.camtech.books.database.DBContract.BookEntry
import java.io.ByteArrayOutputStream
import java.util.*


class Book(var title: String,
           var authors: Array<String>?,
           var publisher: String?,
           var description: String?,
           var smallThumbnail: Bitmap?,
           var thumbnail: Bitmap?,
           var buyLink: String?,
           var apiId: String) {

    private val TAG = Book::class.java.simpleName

    init {
        if (authors == null) authors = arrayOf("Unknown")
        if (publisher == null) publisher = "Unknown"
    }

    // Constants used for parsing the JSON result
    // returned by the Google Books API
    companion object {
        const val VOLUME_INFO = "volumeInfo"
        const val TITLE = "title"
        const val AUTHORS = "authors"
        const val PUBLISHER = "publisher"
        const val IMAGE_LINKS = "imageLinks"
        const val SMALL_THUMBNAIL = "smallThumbnail"
        const val THUMBNAIL = "thumbnail"
        const val DESCRIPTION = "description"
        const val SALE_INFO = "saleInfo"
        const val BUY_LINK = "buyLink"
        const val API_ID = "id"

        fun byteArrayToBitmap(data: ByteArray): Bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    fun addToFavorites(context: Context) {
        // Only add the book to the database if it isn't already there
        if (!isFavorite(context)) {
            try {
                val values = ContentValues()
                values.put(BookEntry.COLUMN_TITLE, title)
                values.put(BookEntry.COLUMN_AUTHORS, Arrays.toString(authors))
                values.put(BookEntry.COLUMN_PUBLISHER, publisher)
                values.put(BookEntry.COLUMN_DESCRIPTION, description)
                values.put(BookEntry.COLUMN_SMALL_THUMBNAIL, bitmapToByteArray(smallThumbnail!!))
                values.put(BookEntry.COLUMN_THUMBNAIL, bitmapToByteArray(thumbnail!!))
                values.put(BookEntry.COLUMN_BUY_LINK, buyLink)
                values.put(BookEntry.COLUMN_API_ID, apiId)
                val uri = context.contentResolver.insert(BookEntry.CONTENT_URI, values)
                Log.i(TAG, "Data inserted successfully: $uri")
            } catch (e: Exception) {
                Log.i(TAG, "Error inserting data", e)
            }
        } else {
            Log.i(TAG, "addToFavorites: Book not added, it already exists")
        }
    }

    fun removeFromFavorites(context: Context, apiId: String) {
        if (isFavorite(context)) {
            val rowsDeleted = context.contentResolver.delete(
                    BookEntry.CONTENT_URI,
                    BookEntry.COLUMN_API_ID + "=\"" + apiId + "\"",
                    null)
            Log.i(TAG, "removeFromFavorites: Rows deleted: $rowsDeleted")
        } else {
            Log.i(TAG, "removeFromFavorites: Nothing was deleted!")
        }
    }

    fun isFavorite(context: Context): Boolean {
        var cursor: Cursor? = null
        var dbHelper: DBHelper? = null
        try {
            dbHelper = DBHelper(context)
            cursor = dbHelper.readableDatabase.query(
                    DBContract.BookEntry.TABLE_NAME,
                    arrayOf(DBContract.BookEntry.COLUMN_API_ID),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)
            if (cursor.moveToFirst()) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val apiInDatabase = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_API_ID))
                    if (apiId == apiInDatabase) {
                        return true
                    }
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.i(TAG, "Error loading data", e)
        } finally {
            cursor?.close()
            dbHelper?.close()
        }
        return false
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}