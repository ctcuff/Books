package com.camtech.books.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.camtech.books.database.DBContract.BookEntry;
import com.camtech.books.database.DBHelper;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Book {

    private static final String TAG = Book.class.getSimpleName();

    // String constants used for parsing JSON
    public static final String VOLUME_INFO = "volumeInfo";
    public static final String TITLE = "title";
    public static final String AUTHORS = "authors";
    public static final String PUBLISHER = "publisher";
    public static final String IMAGE_LINKS = "imageLinks";
    public static final String SMALL_THUMBNAIL = "smallThumbnail";
    public static final String THUMBNAIL = "thumbnail";
    public static final String DESCRIPTION = "description";
    public static final String ACCESS_INFO = "accessInfo";
    public static final String PDF_DOWNLOAD_LINK = "downloadLink";
    public static final String SALE_INFO = "saleInfo";
    public static final String BUY_LINK = "buyLink";
    public static final String API_ID = "id";

    private String title;
    private String[] authors;
    private String publisher;
    private String description;
    private Bitmap smallThumbnail;
    private Bitmap thumbnail;
    private String buyLink;
    private String apiId;

    public Book(String title,
                String[] authors,
                String publisher,
                String description,
                Bitmap smallThumbnail,
                Bitmap thumbnail,
                String buyLink,
                String apiId) {
        this.title = title;

        if (authors != null) this.authors = authors;
        else this.authors = new String[]{"Unknown"};

        if (publisher != null) this.publisher = publisher;
        else this.publisher = "Unknown";

        this.description = description;

        this.smallThumbnail = smallThumbnail;

        this.thumbnail = thumbnail;

        this.buyLink = buyLink;

        this.apiId = apiId;
    }

    public String getTitle() {
        return title;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public Bitmap getSmallThumbnail() {
        return smallThumbnail;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public String getApiId() {
        return apiId;
    }

    public void addToFavorites(Context context) {
        // Only add the book to the database if it isn't already there
        if (!isFavorite(context)) {
            try {
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_TITLE, title);
                values.put(BookEntry.COLUMN_AUTHORS, Arrays.toString(authors));
                values.put(BookEntry.COLUMN_PUBLISHER, publisher);
                values.put(BookEntry.COLUMN_DESCRIPTION, description);
                values.put(BookEntry.COLUMN_SMALL_THUMBNAIL, bitmapToByteArray(smallThumbnail));
                values.put(BookEntry.COLUMN_THUMBNAIL, bitmapToByteArray(thumbnail));
                values.put(BookEntry.COLUMN_BUY_LINK, buyLink);
                values.put(BookEntry.COLUMN_API_ID, apiId);
                Uri uri = context.getContentResolver().insert(BookEntry.CONTENT_URI, values);
                Log.i(TAG, "Data inserted successfully: " + String.valueOf(uri));
            } catch (Exception e) {
                Log.i(TAG, "Error inserting data", e);
            }
        } else {
            Log.i(TAG, "addToFavorites: Book not added, it already exists");
        }
    }

    public void removeFromFavorites(Context context, String apiId) {
        if (isFavorite(context)) {
            int rowsDeleted = context.getContentResolver().delete(
                    BookEntry.CONTENT_URI,
                    BookEntry.COLUMN_API_ID + "=\"" + apiId + "\"",
                    null);
            Log.i(TAG, "removeFromFavorites: ROWS DELETED " + rowsDeleted);
        } else {
            Log.i(TAG, "removeFromFavorites: NOTHING TO DELETE");
        }
    }

    public boolean isFavorite(Context context) {
        Cursor cursor = null;
        DBHelper dbHelper = null;
        try {
            dbHelper = new DBHelper(context);
            cursor = dbHelper.getReadableDatabase().query(
                    BookEntry.TABLE_NAME,
                    new String[]{BookEntry.COLUMN_API_ID},
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String apiIdInDatabase = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_API_ID));
                    if (apiId.equals(apiIdInDatabase)) {
                        return true;
                    }
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "Error loading data", e);
        } finally {
            if (cursor != null) cursor.close();
            if (dbHelper != null) dbHelper.close();
        }
        return false;
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
