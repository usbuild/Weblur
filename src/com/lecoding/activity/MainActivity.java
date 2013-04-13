package com.lecoding.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import com.lecoding.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;

public class MainActivity extends TabActivity {
    /**
     * Called when the activity is first created.
     */
    private final String APP_KEY = "3222108305";
    private final String REDIRECT_URI = "http://usbuild.duapp.com/weiblur.php";
    private Weibo mWeibo;
    public static Oauth2AccessToken token = null;
    public static MainActivity activity;
    private final String ACCESS_TOKEN = "2.008NuYtBnYdDWDd4866062744RXaVC";
    private final String EXPIRES_IN = "157516713";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URI);
        activity = this;

        Resources resources = getResources();

        TextView loginText = (TextView) findViewById(R.id.main_head_title);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeibo.authorize(MainActivity.this, new WeiblurAuthListener());
            }
        });

        MainActivity.token = new Oauth2AccessToken(ACCESS_TOKEN, EXPIRES_IN);

        final TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("Ground").setIndicator(null,
                resources.getDrawable(android.R.drawable.ic_dialog_map)).setContent(
                new Intent(this, GroundActivity.class)
        ));
        tabHost.addTab(tabHost.newTabSpec("Index").setIndicator(null,
                resources.getDrawable(android.R.drawable.ic_dialog_email)).setContent(
                new Intent(this, GroundActivity.class)
        ));
        tabHost.addTab(tabHost.newTabSpec("Info").setIndicator(null,
                resources.getDrawable(android.R.drawable.ic_dialog_info)).setContent(
                new Intent(this, GroundActivity.class)
        ));
    }
}
