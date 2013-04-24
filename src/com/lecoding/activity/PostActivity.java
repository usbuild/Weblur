package com.lecoding.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        postWeibo = (Button) findViewById(R.id.post_weibo);
        postContent = (EditText) findViewById(R.id.post_content);
        statusesAPI = new StatusesAPI(BaseActivity.token);
        postWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusesAPI.update(postContent.getText().toString(), "+30.163663", "+102.930982", new RequestListener() {
                    @Override
                    public void onComplete(String response) {
                        PostActivity.this.finish();
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