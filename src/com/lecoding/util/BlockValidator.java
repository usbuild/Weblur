package com.lecoding.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Handler;
import android.text.Html;
import com.lecoding.data.KeywordProvider;
import com.lecoding.data.SourceProvider;
import com.lecoding.data.Status;

import java.util.List;


/**
 * Created by usbuild on 13-6-18.
 */
public class BlockValidator {
    Context context;
    private List<String> keywords;
    private List<String> sources;
    private static BlockValidator mBlockValidator = null;
    private SharedPreferences preferences;

    public static BlockValidator getInstance(Context context) {
        if (mBlockValidator != null) return mBlockValidator;
        mBlockValidator = new BlockValidator(context);
        return mBlockValidator;
    }

    private BlockValidator(Context context) {
        this.context = context;
        final DuplicateUtil keyUtil = new DuplicateUtil(context, KeywordProvider.traits);
        keywords = keyUtil.getAllKeys();
        preferences = context.getSharedPreferences("block", Context.MODE_PRIVATE);
        context.getContentResolver().registerContentObserver(KeywordProvider.CONTENT_URI, true, new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                keywords = keyUtil.getAllKeys();
            }
        });

        final DuplicateUtil srcUtil = new DuplicateUtil(context, SourceProvider.traits);
        sources = srcUtil.getAllKeys();
        context.getContentResolver().registerContentObserver(SourceProvider.CONTENT_URI, true, new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                sources = srcUtil.getAllKeys();
            }
        });
    }

    public boolean validate(Status status) {
        if (preferences.getBoolean("source", false)) {
            if (sources.contains(Html.fromHtml(status.getSource()).toString())) {
                return false;
            }
        }
        if (preferences.getBoolean("keywords", false)) {

            for (String key : keywords) {
                if (status.getText().contains(key)) return false;
            }
        }
        return true;
    }

}
