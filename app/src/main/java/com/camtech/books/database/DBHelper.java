package com.camtech.books.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.camtech.books.database.DBContract.BookEntry;

public class DBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "books.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_TITLE + " TEXT,"
                + BookEntry.COLUMN_AUTHORS + " TEXT, "
                + BookEntry.COLUMN_PUBLISHER + " TEXT, "
                + BookEntry.COLUMN_DESCRIPTION + " TEXT, "
                + BookEntry.COLUMN_SMALL_THUMBNAIL + " BLOB, "
                + BookEntry.COLUMN_THUMBNAIL + " BLOB, "
                + BookEntry.COLUMN_BUY_LINK + " TEXT, "
                + BookEntry.COLUMN_API_ID + " TEXT);";
        db.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
