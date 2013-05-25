package com.lecoding.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-24 下午7:55
 */
public class SettingActivity extends SherlockPreferenceActivity {

    CheckBoxPreference checkBoxPreference;
    Preference preference;
    SharedPreferences preferences;
    Preference about;

    @SuppressWarnings("deprecated")

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        preferences = getSharedPreferences("setting", MODE_PRIVATE);

        preference = findPreference("pref_key_setting_account");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingActivity.this, AccountActivity.class);
                intent.putExtra("uid", BaseActivity.uid);
                startActivity(intent);
                return true;
            }
        });
        about = findPreference("pref_key_setting_about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
        });

        checkBoxPreference = (CheckBoxPreference) findPreference("pref_key_show_img");
        checkBoxPreference.setChecked(preferences.getBoolean("show_img", true));

        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferences.edit().putBoolean("show_img", checkBoxPreference.isChecked()).commit();
                return true;
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");

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