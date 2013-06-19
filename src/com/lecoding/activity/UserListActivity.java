package com.lecoding.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.data.User;
import com.lecoding.util.JSONParser;
import com.lecoding.util.UserAdapter;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.FriendshipsAPI;
import com.weibo.sdk.android.net.RequestListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by usbuild on 13-5-24.
 */
public class UserListActivity extends SherlockActivity implements GestureDetector.OnGestureListener, View.OnTouchListener {
    ListView listView;
    UserAdapter userAdapter;
    Handler handler;
    GestureDetector gestureDetector;
    UserListInfo listInfo;
    FriendshipsAPI friendshipsAPI;
    long uid;
    final int pageSize = 50;
    public static final int FOLLOWER = 0;
    public static final int FRIEND = 1;
    public static final int BIFOLLOW = 2;
    public int type;
    ProgressDialog progressDialog;

    private final class UserListInfo {
        public List<User> users;
        public int nextCursor;
        public int prevCursor;
        public int total;
    }

    public void updateList() {
        userAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    private RequestListener listener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            updateResult(response);
        }

        @Override
        public void onIOException(IOException e) {

        }

        @Override
        public void onError(WeiboException e) {

        }
    };

    @SuppressWarnings("deprecated")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog = ProgressDialog.show(this, null, "加载中");

        listView = (ListView) findViewById(R.id.user_list);
        listInfo = new UserListInfo();
        listInfo.users = new ArrayList<User>();

        userAdapter = new UserAdapter(this, listInfo.users);
        listView.setAdapter(userAdapter);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                updateList();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UserListActivity.this, AccountActivity.class);
                intent.putExtra("uid", listInfo.users.get(i).getId());
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        uid = intent.getLongExtra("uid", 0);
        type = intent.getIntExtra("type", FOLLOWER);
        friendshipsAPI = new FriendshipsAPI(BaseActivity.token);
        friendsOrFollower(0);
        listView.setOnTouchListener(this);
        gestureDetector = new GestureDetector(this);
        Toast.makeText(this, "手指左右滑动以换页", Toast.LENGTH_SHORT).show();
    }

    public void updateResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            listInfo.nextCursor = jsonObject.getInt("next_cursor");
            listInfo.prevCursor = jsonObject.getInt("previous_cursor");
            listInfo.total = jsonObject.getInt("total_number");
            JSONArray jsonArray = jsonObject.getJSONArray("users");
            listInfo.users.clear();
            for (int i = 0; i < jsonArray.length(); ++i) {
                listInfo.users.add(JSONParser.parseUser(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Message message = new Message();
        handler.sendMessage(message);
    }

    public void loadPrev() {
        if (listInfo.prevCursor == 0) Toast.makeText(getApplicationContext(), "已是第一页", Toast.LENGTH_SHORT).show();
        friendsOrFollower(listInfo.prevCursor);
    }

    public void loadNext() {
        if (listInfo.nextCursor == 0) Toast.makeText(getApplicationContext(), "已是最后一页", Toast.LENGTH_SHORT).show();
        friendsOrFollower(listInfo.nextCursor);

    }

    public void friendsOrFollower(int cursor) {
        switch (type) {
            case FOLLOWER:
                friendshipsAPI.followers(uid, pageSize, cursor, true, listener);
                break;
            case FRIEND:
                friendshipsAPI.friends(uid, pageSize, cursor, true, listener);
                break;
            case BIFOLLOW:
                friendshipsAPI.bilateral(uid, pageSize, cursor, listener);
                break;
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        progressDialog.show();
        if (e1.getX() - e2.getX() > 200) {
            loadNext();
            return true;
        } else if (e1.getX() - e2.getX() < -200) {
            loadPrev();
            return true;
        }
        return false;
    }
}