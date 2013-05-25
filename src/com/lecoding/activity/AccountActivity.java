package com.lecoding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.data.User;
import com.lecoding.util.JSONParser;
import com.loopj.android.image.SmartImageView;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.FriendshipsAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by usbuild on 13-5-23.
 */
public class AccountActivity extends SherlockActivity {

    SmartImageView profileImg;
    Handler handler;
    TextView screenName;
    TextView descreption;
    TextView status;
    Button weiboButton;
    Button followButton;
    Button fansButton;
    Button flwBtn;
    Button msgBtn;
    private static final int LOAD_DATA = 0;
    private static final int FOLLOW = 1;
    private static final int UNFOLLOW = 2;
    User user = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        profileImg = (SmartImageView) findViewById(R.id.profile_img);
        screenName = (TextView) findViewById(R.id.screen_name);
        descreption = (TextView) findViewById(R.id.descreption);
        status = (TextView) findViewById(R.id.status);
        weiboButton = (Button) findViewById(R.id.weibo_btn);
        followButton = (Button) findViewById(R.id.follow_btn);
        fansButton = (Button) findViewById(R.id.fans_btn);
        flwBtn = (Button) findViewById(R.id.befan_btn);
        msgBtn = (Button) findViewById(R.id.pm_btn);

        Intent intent = getIntent();
        long uid = intent.getLongExtra("uid", 0);
        if (uid == 0) {
            Toast.makeText(getApplicationContext(), "用户不存在", Toast.LENGTH_LONG).show();
            this.finish();
        }

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case LOAD_DATA:
                        setData((User) message.obj);
                        break;
                    case FOLLOW:
                        Toast.makeText(getApplicationContext(), "关注成功", Toast.LENGTH_SHORT).show();
                        refreshData(user.getId());
                        break;
                    case UNFOLLOW:
                        Toast.makeText(getApplicationContext(), "取消关注成功", Toast.LENGTH_SHORT).show();
                        refreshData(user.getId());
                        break;
                }
                return true;
            }
        });
        if (uid == BaseActivity.uid) {
            flwBtn.setVisibility(View.GONE);
        }
        msgBtn.setVisibility(View.GONE);

        weiboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, WeiboListActivity.class);
                intent.putExtra("uid", user.getId());
                startActivity(intent);
            }
        });

        fansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, UserListActivity.class);
                intent.putExtra("uid", user.getId());
                intent.putExtra("type", UserListActivity.FOLLOWER);
                startActivity(intent);
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, UserListActivity.class);
                intent.putExtra("uid", user.getId());
                intent.putExtra("type", UserListActivity.FRIEND);
                startActivity(intent);
            }
        });


        final FriendshipsAPI friendshipsAPI = new FriendshipsAPI(BaseActivity.token);
        flwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null)
                    if ((Boolean) flwBtn.getTag()) {
                        friendshipsAPI.create(user.getId(), user.getScreenName(), new RequestListener() {
                            @Override
                            public void onComplete(String response) {
                                Message message = new Message();
                                message.what = FOLLOW;
                                handler.sendMessage(message);
                            }

                            @Override
                            public void onIOException(IOException e) {

                            }

                            @Override
                            public void onError(WeiboException e) {

                            }
                        });
                    } else {
                        friendshipsAPI.destroy(user.getId(), user.getScreenName(), new RequestListener() {
                            @Override
                            public void onComplete(String response) {
                                Message message = new Message();
                                message.what = UNFOLLOW;
                                handler.sendMessage(message);
                            }

                            @Override
                            public void onIOException(IOException e) {

                            }

                            @Override
                            public void onError(WeiboException e) {

                            }
                        });
                    }
            }
        });

        refreshData(uid);
    }

    private void refreshData(long uid) {
        new UsersAPI(BaseActivity.token).show(uid, new RequestListener() {
            @Override
            public void onComplete(String response) {
                try {

                    User user = JSONParser.parseUser(new JSONObject(response));
                    Message message = new Message();
                    message.what = LOAD_DATA;
                    message.obj = user;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_LONG).show();
                    AccountActivity.this.finish();
                    Looper.loop();
                }
            }

            @Override
            public void onIOException(IOException e) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_LONG).show();
                AccountActivity.this.finish();
                Looper.loop();
            }

            @Override
            public void onError(WeiboException e) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "获取用户信息失败", Toast.LENGTH_LONG).show();
                AccountActivity.this.finish();
                Looper.loop();
            }
        });
    }

    public void setData(User user) {
        this.user = user;
        getSupportActionBar().setTitle(user.getScreenName());
        profileImg.setImageUrl(user.getProfileImageUrl());
        screenName.setText(user.getScreenName());
        descreption.setText(user.getDescription());
        status.setText(user.getStatus() != null ? user.getStatus().getText() : "");
        weiboButton.setText("微博\n" + user.getStatusesCount());
        followButton.setText("关注\n" + user.getFriendsCount());
        fansButton.setText("粉丝\n" + user.getFollowersCount());


        if (user.isFollowing()) {
            flwBtn.setText("取消关注");
            flwBtn.setTag(false);
        } else {
            flwBtn.setText("关注");
            flwBtn.setTag(true);
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