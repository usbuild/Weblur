package com.lecoding.activity;

import android.os.Bundle;
import com.lecoding.data.KeywordProvider;
import com.lecoding.util.KeywordUtil;

/**
 * Created by usbuild on 13-6-17.
 */
public class KeywordActivity extends AbstractListViewActivity {
    KeywordUtil util;

    public KeywordActivity() {
        super(KeywordProvider.traits);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("关键词");
        util = new KeywordUtil(this);
    }

    @Override
    protected boolean isTaken(String data) {
        return util.hasKey(data);
    }
}