package com.lecoding.activity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.util.GPSTracker;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-24 上午10:18
 */
public class PostActivity extends SherlockActivity {
    private Button postWeibo;
    private EditText postContent;
    private StatusesAPI statusesAPI;
    private Button positionButton;
    private String latitude = "";
    private String longitude = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        positionButton = (Button) findViewById(R.id.use_position);
        postWeibo = (Button) findViewById(R.id.post_weibo);
        postContent = (EditText) findViewById(R.id.post_content);
        statusesAPI = new StatusesAPI(BaseActivity.token);

        final GPSTracker gpsTracker = new GPSTracker(this);
        postWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusesAPI.update(postContent.getText().toString(), latitude, longitude, new RequestListener() {
                    @Override
                    public void onComplete(String response) {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "发表成功", Toast.LENGTH_LONG).show();
                        PostActivity.this.finish();
                        Looper.loop();
                    }

                    @Override
                    public void onIOException(IOException e) {

                    }

                    @Override
                    public void onError(WeiboException e) {

                    }
                });
            }
        });

        positionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gpsTracker.canGetLocation()) {
                    if (gpsTracker.canGetLocation()) {
                        latitude = String.valueOf(gpsTracker.getLatitude());
                        longitude = String.valueOf(gpsTracker.getLongitude());
                        Toast.makeText(getApplicationContext(), "定位成功", Toast.LENGTH_LONG).show();
                    } else {
                        gpsTracker.showSettingsAlert();
                    }
                }
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