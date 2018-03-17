package com.camtech.books.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.camtech.books.database.DBContract.BookEntry

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "books.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_TABLE = ("CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_TITLE + " TEXT,"
                + BookEntry.COLUMN_AUTHORS + " TEXT, "
                + BookEntry.COLUMN_PUBLISHER + " TEXT, "
                + BookEntry.COLUMN_DESCRIPTION + " TEXT, "
                + BookEntry.COLUMN_SMALL_THUMBNAIL + " BLOB, "
                + BookEntry.COLUMN_THUMBNAIL + " BLOB, "
                + BookEntry.COLUMN_BUY_LINK + " TEXT, "
                + BookEntry.COLUMN_API_ID + " TEXT);")
        db.execSQL(SQL_CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}
