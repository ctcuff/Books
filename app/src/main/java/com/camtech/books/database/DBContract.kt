package com.camtech.books.database

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

object DBContract {

    val CONTENT_AUTHORITY = "com.camtech.books"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://" + CONTENT_AUTHORITY)
    val PATH_BOOKS = "books"

    class BookEntry : BaseColumns {
        companion object {
            val CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS
            val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS
            val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS)
            val TABLE_NAME = "bookdata"

            val _ID = BaseColumns._ID
            val COLUMN_TITLE = "title"
            val COLUMN_AUTHORS = "authors"
            val COLUMN_PUBLISHER = "publisher"
            val COLUMN_DESCRIPTION = "description"
            val COLUMN_SMALL_THUMBNAIL = "smallthumbnail"
            val COLUMN_THUMBNAIL = "thumbnail"
            val COLUMN_BUY_LINK = "buylink"
            val COLUMN_API_ID = "api_id"
        }

    }
}