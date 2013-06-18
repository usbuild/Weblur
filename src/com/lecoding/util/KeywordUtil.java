package com.lecoding.util;

import android.content.Context;
import android.database.Cursor;
import com.lecoding.data.KeywordProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usbuild on 13-6-18.
 */
public class KeywordUtil {
    Context context;

    public KeywordUtil(Context context) {
        this.context = context;
    }

    public boolean hasKey(String data) {
        Cursor cursor = context.getContentResolver().query(KeywordProvider.CONTENT_URI, null, KeywordProvider.NAME + "=?", new String[]{data}, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public List<String> getAllKeys() {
        Cursor cursor = context.getContentResolver().query(KeywordProvider.CONTENT_URI, null, null, null, null);
        List<String> keys = new ArrayList<String>(cursor.getCount());
        int column = cursor.getColumnIndex(KeywordProvider.NAME);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            keys.add(cursor.getString(column));
        }
        return keys;
    }
}
