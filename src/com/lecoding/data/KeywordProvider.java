package com.lecoding.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

/**
 * Created by usbuild on 13-6-17.
 */
public class KeywordProvider extends ContentProvider {

    private static final UriMatcher URI_MATCHER;
    private static final int ALL_KEYS = 0;
    private static final int ONE_KEY = 1;


    public static final String AUTHORITY = "com.lecoding.data.keywordprovider";
    public static final String BASE_PATH = "keywords";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, ALL_KEYS);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/#", ONE_KEY);
    }

    public static final String NAME = "name";
    private static SQLiteDatabase database;
    private static final String TBNAME = "keywords";


    @Override
    public boolean onCreate() {
        database = new KeyDataBaseHelper(getContext()).getWritableDatabase();
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TBNAME);
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private static class KeyDataBaseHelper extends SQLiteOpenHelper {
        private final static String DBNAME = "keywords.db";
        private final static String _ID = "_id";
        private static final int DATABASE_VERSION = 2;

        public KeyDataBaseHelper(Context context) {
            super(context, DBNAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table if not exists " + TBNAME + "(" + _ID + " integer primary key autoincrement, name text not null);");

            SQLiteStatement statement = sqLiteDatabase.compileStatement("insert into " + TBNAME + "(name) values(\"hello\")");
            statement.executeInsert();
            statement.close();

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("drop table if exists " + DBNAME);
            onCreate(sqLiteDatabase);
        }
    }
}
