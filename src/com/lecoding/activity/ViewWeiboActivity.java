package com.lecoding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.data.Comment;
import com.lecoding.data.PicDetail;
import com.lecoding.data.Status;
import com.lecoding.util.CommentAdapter;
import com.lecoding.util.JSONParser;
import com.lecoding.util.RepostAdapter;
import com.lecoding.view.PicList;
import com.lecoding.view.Retweet;
import com.loopj.android.image.SmartImageView;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-18 上午9:38
 */
public class ViewWeiboActivity extends SherlockActivity {
    private final int UPDATE_CMT = 0;
    private final int UPDATE_REPO = 1;

    private TextView weiboText;
    private SmartImageView profileImg;
    private SmartImageView thumbnail;
    private PicList picList;
    private Retweet retweet;
    private TextView commentCount;
    private TextView repostCount;
    private TextView attitudeCount;
    private ListView commentList;
    private CommentAdapter commentAdapter;
    private RepostAdapter repostAdapter;
    private List<Comment> comments;
    private List<Status> reposts;
    private Status status;
    public final static int COMMENT = 0;
    public final static int REPOST = 1;
    private int type = COMMENT;

    private Handler handler;


    public void updateCommentList(List<Comment> comments) {
        commentList.setAdapter(new CommentAdapter(this, comments));
        commentCount.setTextColor(getResources().getColor(R.color.lightblue));
        repostCount.setTextColor(getResources().getColor(android.R.color.black));
        type = COMMENT;
    }

    public void updateRepostList(List<Status> statuses) {
        commentList.setAdapter(new RepostAdapter(this, statuses));
        repostCount.setTextColor(getResources().getColor(R.color.lightblue));
        commentCount.setTextColor(getResources().getColor(android.R.color.black));
        type = REPOST;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        commentList = new ListView(this);
        //set list options
        commentList.setHeaderDividersEnabled(false);
        commentList.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View view = getLayoutInflater().inflate(R.layout.view_weibo, null);
        commentList.addHeaderView(view);

        setContentView(commentList);

        repostAdapter = new RepostAdapter(this, reposts);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        comments = new ArrayList<Comment>();
        reposts = new ArrayList<Status>();

        weiboText = (TextView) findViewById(R.id.weibo_item_text);
        profileImg = (SmartImageView) findViewById(R.id.profile_img);


        thumbnail = (SmartImageView) findViewById(R.id.thumbnail);
        picList = (PicList) findViewById(R.id.piclist);
        retweet = (Retweet) findViewById(R.id.retweet);

        attitudeCount = (TextView) findViewById(R.id.weibo_item_attitude_cnt);
        commentCount = (TextView) findViewById(R.id.weibo_item_comment_cnt);
        repostCount = (TextView) findViewById(R.id.weibo_item_repost_cnt);


        status = (Status) getIntent().getSerializableExtra("status");

        attitudeCount.setText("赞(" + status.getAttitudesCount() + ")");
        commentCount.setText("评论(" + status.getCommentsCount() + ")");
        repostCount.setText("转发(" + status.getRepostsCount() + ")");

        weiboText.setText(status.getText());
        profileImg.setImageUrl(status.getUser().getProfileImageUrl());

        List<PicDetail> picDetails = status.getPicDetails();

        if (status.getRetweetedStatus() != null) {
            retweet.setVisibility(View.VISIBLE);
            retweet.setData(status.getRetweetedStatus(), true);
        } else {
            if (picDetails.size() <= 1) {
                thumbnail.setImageUrl(status.getBmiddlePic());
                thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewWeiboActivity.this, ViewImageActivity.class);
                        intent.putExtra("uri", status.getOriginalPic());
                        startActivity(intent);
                    }
                });
            } else {
                List<String> urls = new ArrayList<String>();
                for (PicDetail picDetail : picDetails) {
                    urls.add(picDetail.getThumbnailPic());
                }
                picList.setImages(urls);
            }
        }

        handler = new Handler(new Handler.Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case UPDATE_CMT:
                        updateCommentList((List<Comment>) message.obj);
                        break;
                    case UPDATE_REPO:
                        updateRepostList((List<Status>) message.obj);
                        break;

                }
                return false;
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewWeiboActivity.this, AccountActivity.class);
                intent.putExtra("uid", status.getUser().getId());
                startActivity(intent);
            }
        });

        repostCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadReposts();
            }
        });

        commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadComments();
            }
        });

        attitudeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        commentAdapter = new CommentAdapter(this, comments);
        commentList.setAdapter(commentAdapter);

        loadComments();

        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (type == COMMENT) {

                    Comment comment = (Comment) commentList.getAdapter().getItem(i);
                    Intent intent = new Intent(ViewWeiboActivity.this, AccountActivity.class);
                    intent.putExtra("uid", comment.getUser().getId());
                    startActivity(intent);
                } else {
                    Status status = (Status) commentList.getAdapter().getItem(i);
                    Intent intent = new Intent(ViewWeiboActivity.this, ViewWeiboActivity.class);
                    intent.putExtra("status", status);
                    startActivity(intent);
                }
            }
        });
    }

    public void loadReposts() {
        StatusesAPI statusesAPI = new StatusesAPI(BaseActivity.token);
        statusesAPI.repostTimeline(status.getId(), 0, 0, 20, 1, WeiboAPI.AUTHOR_FILTER.ALL, new RequestListener() {
            @Override
            public void onComplete(String response) {
                JSONTokener tokener = new JSONTokener(response);
                try {
                    JSONArray jsonArray = ((JSONObject) tokener.nextValue()).getJSONArray("reposts");
                    List<Status> statuses = new ArrayList<Status>();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        statuses.add(JSONParser.parseStatus(jsonObject));
                    }

                    Message message = new Message();
                    message.obj = statuses;
                    message.what = UPDATE_REPO;
                    handler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIOException(IOException e) {

            }

            @Override
            public void onError(WeiboException e) {

            }
        });
    }

    public void loadComments() {
        CommentsAPI commentsAPI = new CommentsAPI(BaseActivity.token);
        commentsAPI.show(status.getId(), 0, 0, 20, 1,
                WeiboAPI.AUTHOR_FILTER.ALL
                , new RequestListener() {
            @Override
            public void onComplete(String response) {
                JSONTokener tokener = new JSONTokener(response);
                try {
                    JSONArray jsonArray = ((JSONObject) tokener.nextValue()).getJSONArray("comments");
                    List<Comment> comments = new ArrayList<Comment>();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        comments.add(JSONParser.parseComment(jsonObject));
                    }

                    Message message = new Message();
                    message.obj = comments;
                    message.what = UPDATE_CMT;
                    handler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIOException(IOException e) {

            }

            @Override
            public void onError(WeiboException e) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.weibo, menu);
        return super.onCreateOptionsMenu(menu);
    }
}