package com.lecoding.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.data.PicDetail;
import com.lecoding.data.Status;
import com.lecoding.view.PicList;
import com.lecoding.view.Retweet;
import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-18 上午9:38
 */
public class ViewWeiboActivity extends SherlockActivity {
    private TextView weiboText;
    private SmartImageView profileImg;
    private SmartImageView thumbnail;
    private PicList picList;
    private Retweet retweet;
    private TextView commentCount;
    private TextView repostCount;
    private TextView attitudeCount;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_weibo);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        weiboText = (TextView) findViewById(R.id.weibo_item_text);
        profileImg = (SmartImageView) findViewById(R.id.profile_img);
        thumbnail = (SmartImageView) findViewById(R.id.thumbnail);
        picList = (PicList) findViewById(R.id.piclist);
        retweet = (Retweet) findViewById(R.id.retweet);

        attitudeCount = (TextView) findViewById(R.id.weibo_item_attitude_cnt);
        commentCount = (TextView) findViewById(R.id.weibo_item_comment_cnt);
        repostCount = (TextView) findViewById(R.id.weibo_item_repost_cnt);

        Status status = (Status) getIntent().getSerializableExtra("status");

        attitudeCount.setText("赞(" + status.getAttitudesCount() + ")");
        commentCount.setText("评论(" + status.getCommentsCount() + ")");
        repostCount.setText("转发(" + status.getRepostsCount() + ")");

        weiboText.setText(status.getText());
        profileImg.setImageUrl(status.getUser().getProfileImageUrl());


        if (status.getRetweetedStatus() != null) {
            retweet.setVisibility(View.VISIBLE);
            retweet.setData(status.getRetweetedStatus());
        } else {
            thumbnail.setImageUrl(status.getBmiddlePic());
        }

        List<PicDetail> picDetails = status.getPicDetails();
        if (picDetails.size() > 0) {
            List<String> urls = new ArrayList<String>();
            for (PicDetail picDetail : picDetails) {
                urls.add(picDetail.getThumbnailPic());
            }
            picList.setImages(urls);
        }
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