package com.lecoding.util;

import android.content.Context;
import android.database.Cursor;
import com.lecoding.data.ContentProviderTraits;
import com.lecoding.data.KeywordProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usbuild on 13-6-18.
 */
public class DuplicateUtil {
    Context context;
    ContentProviderTraits traits;

    public DuplicateUtil(Context context, ContentProviderTraits traits) {
        this.context = context;
        this.traits = traits;
    }

    public boolean hasKey(String data) {
        Cursor cursor = context.getContentResolver().query(traits.getContentUri(), null, traits.getNAME() + "=?", new String[]{data}, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public List<String> getAllKeys() {
        Cursor cursor = context.getContentResolver().query(traits.getContentUri(), null, null, null, null);
        List<String> keys = new ArrayList<String>(cursor.getCount());
        int column = cursor.getColumnIndex(traits.getNAME());
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            keys.add(cursor.getString(column));
        }
        return keys;
    }
}
