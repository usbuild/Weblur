package com.lecoding.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
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
public class UserListActivity extends Activity {
    ListView listView;
    UserAdapter userAdapter;
    List<User> users;
    Handler handler;

    private final class UserListInfo {
        public List<User> users;
        public int nextCursor;
        public int prevCursor;
        public int total;
    }

    public void updateList(UserListInfo listInfo) {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        listView = (ListView) findViewById(R.id.user_list);
        users = new ArrayList<User>();
        userAdapter = new UserAdapter(this, users);
        listView.setAdapter(userAdapter);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                updateList((UserListInfo) message.obj);
                return true;
            }
        });

        Intent intent = getIntent();
        final long uid = intent.getLongExtra("uid", 0);
        FriendshipsAPI friendshipsAPI = new FriendshipsAPI(BaseActivity.token);
        final int pageSize = 50;
        friendshipsAPI.followers(uid, pageSize, 0, true, new RequestListener() {
            @Override
            public void onComplete(String response) {
                UserListInfo listInfo = new UserListInfo();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    listInfo.nextCursor = jsonObject.getInt("next_cursor");
                    listInfo.prevCursor = jsonObject.getInt("previous_cursor");
                    listInfo.total = jsonObject.getInt("total_number");
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    listInfo.users = new ArrayList<User>(jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        listInfo.users.add(JSONParser.parseUser(jsonArray.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message message = new Message();
                message.obj = listInfo;
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