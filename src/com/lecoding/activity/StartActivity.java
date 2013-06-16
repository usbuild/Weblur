package com.lecoding.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.lecoding.R;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-11 下午2:33
 */
public class StartActivity extends Activity {
    private static final int SPLASH_DISPLAY_TIME = 2000; /* 3 seconds */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(StartActivity.this,
                        BaseActivity.class);
                StartActivity.this.startActivity(mainIntent);
                StartActivity.this.finish();
                overridePendingTransition(R.anim.mainfadein,
                        R.anim.splashfadeout);
            }
        }, SPLASH_DISPLAY_TIME);
    }
}