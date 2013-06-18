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
import android.text.TextUtils;

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

    public static final String _ID = "_id";
    public static final String NAME = "name";
    private KeyDataBaseHelper helper;


    @Override
    public boolean onCreate() {
        helper = new KeyDataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = helper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(KeyDataBaseHelper.TBNAME);
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, "name asc");

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = helper.getWritableDatabase();
        long id = 0;
        switch (URI_MATCHER.match(uri)) {
            case ALL_KEYS:
                id = database.insert(KeyDataBaseHelper.TBNAME, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowDeleted;
        switch (URI_MATCHER.match(uri)) {
            case ALL_KEYS:
                rowDeleted = database.delete(KeyDataBaseHelper.TBNAME, selection, selectionArgs);
                break;
            case ONE_KEY:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowDeleted = database.delete(KeyDataBaseHelper.TBNAME, KeyDataBaseHelper._ID + "=" + id, null);
                } else {
                    rowDeleted = database.delete(KeyDataBaseHelper.TBNAME, KeyDataBaseHelper._ID + "=" + id
                            + " and " + selection
                            , selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowUpdated;
        switch (URI_MATCHER.match(uri)) {
            case ALL_KEYS:
                rowUpdated = database.update(KeyDataBaseHelper.TBNAME, contentValues, selection, selectionArgs);
                break;
            case ONE_KEY:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowUpdated = database.update(KeyDataBaseHelper.TBNAME, contentValues, KeyDataBaseHelper._ID + "=" + id, null);
                } else {
                    rowUpdated = database.update(KeyDataBaseHelper.TBNAME, contentValues, KeyDataBaseHelper._ID + "=" + id
                            + " and " + selection
                            , selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return rowUpdated;
    }

    private static class KeyDataBaseHelper extends SQLiteOpenHelper {
        private final static String DBNAME = "keywords.db";
        private final static String TBNAME = "keywords";
        private final static String _ID = "_id";
        private static final int DATABASE_VERSION = 2;

        public KeyDataBaseHelper(Context context) {
            super(context, DBNAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table if not exists " + TBNAME + "(" + _ID + " integer primary key autoincrement, name text not null);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("drop table if exists " + DBNAME);
            onCreate(sqLiteDatabase);
        }
    }
}
