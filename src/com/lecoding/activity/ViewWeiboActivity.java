package com.lecoding.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.lecoding.R;
import com.lecoding.data.Status;
import com.loopj.android.image.SmartImageView;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-18 上午9:38
 */
public class ViewWeiboActivity extends Activity {
    private TextView weiboText;
    private SmartImageView profileImg;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_weibo);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        weiboText = (TextView) findViewById(R.id.weibo_item_text);
        profileImg = (SmartImageView) findViewById(R.id.profile_img);

        Status status = (Status) getIntent().getSerializableExtra("status");
        weiboText.setText(status.getText());
        profileImg.setImageUrl(status.getUser().getProfileImageUrl());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ViewWeiboActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}