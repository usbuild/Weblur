package com.lecoding.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.loopj.android.image.WebImageCache;


/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-24 下午7:55
 */
public class SettingActivity extends SherlockPreferenceActivity {

    //    CheckBoxPreference showImagePref;
    Preference accountPref;
    SharedPreferences preferences;
    Preference about;
    Preference logoutPref;
    Preference blockPref;
    Preference clearCache;
    Handler handler;
    WebImageCache imageCache;
    public final int UPDATING = 0;
    public final int UPDATED = 1;

    @SuppressWarnings("deprecated")

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        preferences = getSharedPreferences("setting", MODE_PRIVATE);


        accountPref = findPreference("pref_key_setting_account");
        accountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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

        logoutPref = findPreference("pref_key_setting_logout");
        logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("退出登录?");
                builder.setMessage("您将要重新输入用户名和密码以使用本程序");
                builder.setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CookieSyncManager.createInstance(SettingActivity.this);
                                CookieSyncManager.getInstance().startSync();
                                CookieManager.getInstance().removeAllCookie();
                                BaseActivity.token = null;

                                Intent intent = new Intent(SettingActivity.this, BaseActivity.class);
                                intent.putExtra("exit", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                builder.create().show();

                return true;
            }
        });
        blockPref = findPreference("pref_key_setting_block");
        blockPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingActivity.this, BlockActivity.class);
                startActivity(intent);
                return true;
            }
        });

        clearCache = findPreference("pref_key_setting_cache");
        imageCache = new WebImageCache(SettingActivity.this);

        clearCache.setSummary("空间占用： 正在计算");
        new UpdateCacheTask().execute(false);

        clearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new UpdateCacheTask().execute(true);
                return true;
            }
        });



        /*
        showImagePref = (CheckBoxPreference) findPreference("pref_key_show_img");
        showImagePref.setChecked(preferences.getBoolean("show_img", true));

        showImagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferences.edit().putBoolean("show_img", showImagePref.isChecked()).commit();
                return true;
            }
        });
        */


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case UPDATED:
                        clearCache.setSummary("空间占用: " + message.obj.toString());
                        clearCache.setEnabled(true);
                        break;
                    case UPDATING:
                        clearCache.setSummary("正在清除");
                        clearCache.setEnabled(false);
                        break;
                }
                return true;
            }
        });

    }

    class UpdateCacheTask extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... v) {
            Message message = new Message();
            message.what = UPDATED;
            if (v[0]) {
                handler.sendEmptyMessage(UPDATING);
                imageCache.clear();
            }
            message.obj = imageCache.cacheSize();
            handler.sendMessage(message);
            return null;
        }
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