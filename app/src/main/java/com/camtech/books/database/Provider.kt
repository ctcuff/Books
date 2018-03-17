package com.camtech.books.database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.camtech.books.database.DBContract.BookEntry

class Provider : ContentProvider() {

    lateinit var dbHelper: DBHelper

    companion object {
        private val TAG = Provider::class.java.simpleName
        private val BOOKS = 100
        private val BOOK_ID = 101

        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_BOOKS, BOOKS)
            sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_BOOKS + "/#", BOOK_ID)
        }
    }

    override fun onCreate(): Boolean {
        dbHelper = DBHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, _selection: String?, _selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        var selection = _selection
        var selectionArgs = _selectionArgs
        val database = dbHelper.readableDatabase
        val cursor: Cursor
        when (sUriMatcher.match(uri)) {
            BOOKS -> cursor = database.query(
                    BookEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs, null, null,
                    sortOrder)
            BOOK_ID -> {
                selection = BookEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database.query(
                        BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null,
                        sortOrder)
            }
            else -> throw IllegalArgumentException("Cannot query, unknown URI " + uri)
        }
        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        when (sUriMatcher.match(uri)) {
            BOOKS -> return insertBookData(uri, contentValues)
            else -> throw IllegalArgumentException("Insertion is not supported for " + uri)
        }
    }

    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)
        return when (match) {
            BOOKS -> BookEntry.CONTENT_LIST_TYPE
            BOOK_ID -> BookEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }


    override fun delete(uri: Uri, _selection: String?, _selectionArgs: Array<String>?): Int {
        var selection = _selection
        var selectionArgs = _selectionArgs
        val database = dbHelper.writableDatabase

        val rowsDeleted: Int

        val match = sUriMatcher.match(uri)
        when (match) {
            BOOKS -> rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs)
            BOOK_ID -> {
                selection = BookEntry.COLUMN_API_ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for " + uri)
        }
        if (rowsDeleted != 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun update(uri: Uri, contentValues: ContentValues?, _selection: String?, _selectionArgs: Array<String>?): Int {
        var selection = _selection
        var selectionArgs = _selectionArgs
        return when (sUriMatcher.match(uri)) {
            BOOKS -> updateBookData(uri, contentValues, selection, selectionArgs)
            BOOK_ID -> {
                selection = BookEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                updateBookData(uri, contentValues, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Update is not supported for " + uri)
        }
    }

    private fun insertBookData(uri: Uri, values: ContentValues?): Uri? {
        val database = dbHelper.writableDatabase
        val id = database.insert(BookEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1L) {
            Log.e(TAG, "Failed to insert row for " + uri)
            return null
        }
        // Notify listeners that the data has changed
        context.contentResolver.notifyChange(uri, null)

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }

    private fun updateBookData(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        if (values?.size() == 0) return 0
        val database = dbHelper.writableDatabase
        val rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs)
        if (rowsUpdated != 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return rowsUpdated
    }
}