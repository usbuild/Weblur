package com.lecoding.activity;

import android.app.Activity;
import android.os.Bundle;
import com.lecoding.data.KeywordProvider;
import com.lecoding.data.SourceProvider;
import com.lecoding.util.DuplicateUtil;

/**
 * Created by usbuild on 13-6-18.
 */
public class SourceActivity extends AbstractListViewActivity {
    DuplicateUtil util;

    public SourceActivity() {
        super(SourceProvider.traits);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("微博来源");
        util = new DuplicateUtil(this, SourceProvider.traits);
    }

    @Override
    protected boolean isTaken(String data) {
        return util.hasKey(data);
    }
}