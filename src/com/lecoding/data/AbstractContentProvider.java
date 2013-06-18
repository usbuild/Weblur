package com.lecoding.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by usbuild on 13-6-18.
 */
public abstract class AbstractContentProvider extends ContentProvider {

    public static final int ALL_KEYS = 0;
    public static final int ONE_KEY = 1;

    ContentProviderTraits traits;

    protected AbstractContentProvider(ContentProviderTraits traits) {
        this.traits = traits;
    }

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
        queryBuilder.setTables(traits.getTable());
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
        switch (traits.getUriMatcher().match(uri)) {
            case ALL_KEYS:
                id = database.insert(traits.getTable(), null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(traits.getContentUri(), null);
        return Uri.parse(traits.getBasePath() + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowDeleted;
        switch (traits.getUriMatcher().match(uri)) {
            case ALL_KEYS:
                rowDeleted = database.delete(traits.getTable(), selection, selectionArgs);
                break;
            case ONE_KEY:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowDeleted = database.delete(traits.getTable(), traits.get_ID() + "=" + id, null);
                } else {
                    rowDeleted = database.delete(traits.getTable(), traits.get_ID() + "=" + id
                            + " and " + selection
                            , selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(traits.getContentUri(), null);
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowUpdated;
        switch (traits.getUriMatcher().match(uri)) {
            case ALL_KEYS:
                rowUpdated = database.update(traits.getTable(), contentValues, selection, selectionArgs);
                break;
            case ONE_KEY:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowUpdated = database.update(traits.getTable(), contentValues, traits.get_ID() + "=" + id, null);
                } else {
                    rowUpdated = database.update(traits.getTable(), contentValues, traits.get_ID() + "=" + id
                            + " and " + selection
                            , selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(traits.getContentUri(), null);
        return rowUpdated;
    }

    private class KeyDataBaseHelper extends SQLiteOpenHelper {
        private static final String DBNAME = "block.db";
        private static final int DATABASE_VERSION = 2;

        public KeyDataBaseHelper(Context context) {
            super(context, DBNAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table if not exists keywords(_id integer primary key autoincrement, name text not null);");
            sqLiteDatabase.execSQL("create table if not exists source(_id integer primary key autoincrement, name text not null);");
            sqLiteDatabase.execSQL("create table if not exists time(_id integer primary key autoincrement, name text not null);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("drop table if exists " + DBNAME);
            onCreate(sqLiteDatabase);
        }
    }
}
