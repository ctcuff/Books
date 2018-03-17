package com.camtech.books.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBContract {

    public static final String CONTENT_AUTHORITY = "com.camtech.books";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final  String PATH_BOOKS = "books";

    private DBContract(){}

    public static final class BookEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String TABLE_NAME = "bookdata";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHORS = "authors";
        public static final String COLUMN_PUBLISHER = "publisher";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_SMALL_THUMBNAIL = "smallthumbnail";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_BUY_LINK = "buylink";
        public static final String COLUMN_API_ID = "api_id";

    }
}
