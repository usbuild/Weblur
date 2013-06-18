package com.lecoding.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.view.MyCheckboxPreference;

/**
 * Created by usbuild on 13-6-18.
 */
public class BlockActivity extends SherlockPreferenceActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("屏蔽设置");

        addPreferencesFromResource(R.xml.block);
        MyCheckboxPreference keyword = (MyCheckboxPreference) findPreference("pref_block_keyword");

        preferences = getSharedPreferences("block", MODE_PRIVATE);

        boolean enableKeywords = preferences.getBoolean("keywords", false);
        keyword.setChecked(enableKeywords);
        keyword.setOnSetClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlockActivity.this, KeywordActivity.class);
                startActivity(intent);
            }
        });
        keyword.setOnChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean("keywords", b).commit();
            }
        });

        MyCheckboxPreference source = (MyCheckboxPreference) findPreference("pref_block_source");
        boolean enableSource = preferences.getBoolean("source", false);
        source.setChecked(enableSource);
        source.setOnSetClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlockActivity.this, SourceActivity.class);
                startActivity(intent);
            }
        });
        source.setOnChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean("source", b).commit();
            }
        });

        MyCheckboxPreference time = (MyCheckboxPreference) findPreference("pref_block_time");
        boolean enableTime = preferences.getBoolean("time", false);
        time.setChecked(enableTime);
        time.setOnSetClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        time.setOnChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean("time", b).commit();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}