package com.lecoding.activity;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.view.TouchImageView;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-5-3 下午3:24
 */
public class ViewImageActivity extends SherlockActivity {

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");


        setContentView(R.layout.view_image);

        TouchImageView touchImageView = (TouchImageView) findViewById(R.id.snoop);

        touchImageView.setImageUrl(uri);
        touchImageView.setMaxZoom(4f);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(uri);

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