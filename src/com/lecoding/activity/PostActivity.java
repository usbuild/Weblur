package com.lecoding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.data.Status;
import com.lecoding.util.GPSTracker;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
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

    private Button positionButton;
    private String latitude = "";
    private String longitude = "";

    public final static int COMMENT = 0;
    public final static int FORWARD = 1;
    public final static int STATUS = 2;
    private int type = COMMENT;
    private Status status;

    private CheckBox commentOrigin;
    private CheckBox commentCurrent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", COMMENT);
        if (type == COMMENT || type == FORWARD) {
            status = (Status) intent.getSerializableExtra("status");
        }

        positionButton = (Button) findViewById(R.id.use_position);
        postWeibo = (Button) findViewById(R.id.post_weibo);
        postContent = (EditText) findViewById(R.id.post_content);

        commentOrigin = (CheckBox) findViewById(R.id.comment_origin);
        commentCurrent = (CheckBox) findViewById(R.id.comment_current);

        if (type == STATUS) {
            positionButton.setVisibility(View.GONE);
            commentCurrent.setVisibility(View.GONE);
            commentOrigin.setVisibility(View.GONE);
            postStatus();
            actionBar.setTitle("发表微博");
        } else if (type == COMMENT) {
            positionButton.setVisibility(View.GONE);
            commentCurrent.setVisibility(View.GONE);
            commentStatus(status);
            actionBar.setTitle("评论: " + status.getText());
        } else if (type == FORWARD) {
            positionButton.setVisibility(View.GONE);
            forwardStatus(status);
            actionBar.setTitle("转发: " + status.getText());
        }

    }

    public void postStatus() {
        final StatusesAPI statusesAPI;
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

    public void forwardStatus(final Status status) {

        postWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusesAPI statusesAPI = new StatusesAPI(BaseActivity.token);

                WeiboAPI.COMMENTS_TYPE type = WeiboAPI.COMMENTS_TYPE.NONE;
                if (commentOrigin.isChecked() && commentCurrent.isChecked()) {
                    type = WeiboAPI.COMMENTS_TYPE.BOTH;
                } else if (commentCurrent.isChecked()) {
                    type = WeiboAPI.COMMENTS_TYPE.CUR_STATUSES;
                } else if (commentOrigin.isChecked()) {
                    type = WeiboAPI.COMMENTS_TYPE.ORIGAL_STATUSES;
                }

                statusesAPI.repost(status.getId(), postContent.getText().toString(), type, new RequestListener() {
                    @Override
                    public void onComplete(String response) {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "转发成功", Toast.LENGTH_LONG).show();
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


    }

    public void commentStatus(final Status status) {
        postWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentsAPI commentsAPI = new CommentsAPI(BaseActivity.token);
                commentsAPI.create(postContent.getText().toString(), status.getId(), commentOrigin.isChecked(), new RequestListener() {
                    @Override
                    public void onComplete(String response) {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "发表评论成功", Toast.LENGTH_LONG).show();
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