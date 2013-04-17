package com.lecoding.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.lecoding.R;
import com.lecoding.data.Status;
import com.lecoding.util.JSONParser;
import com.lecoding.util.WeiboAdapter;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private final String APP_KEY = "3222108305";
    private final String REDIRECT_URI = "http://usbuild.duapp.com/weiblur.php";
    private Weibo mWeibo;
    public static Oauth2AccessToken token = null;
    public static MainActivity activity;
    private final String ACCESS_TOKEN = "2.008NuYtBnYdDWDd4866062744RXaVC";
    private final String EXPIRES_IN = "157516713";


    private final int WEIBO_ERROR = 0;
    private final int PUBLIC_LINE = 1;
    Handler handler = null;
    private ListView listView;
    private ProgressDialog progressDialog;

    public void updateListView(List<Status> statuses) {
        listView.setAdapter(new WeiboAdapter(this, statuses));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URI);
        activity = this;
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        MainActivity.token = new Oauth2AccessToken(ACCESS_TOKEN, EXPIRES_IN);

//////////////
        listView = (ListView) findViewById(R.id.ground_list);

        handler = new Handler(new Handler.Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean handleMessage(Message message) {
                progressDialog.cancel();
                switch (message.what) {
                    case WEIBO_ERROR:
                        Toast.makeText(MainActivity.this, "拉取微博信息出错！", Toast.LENGTH_LONG).show();
                        break;
                    case PUBLIC_LINE:
                        updateListView((List<Status>) message.obj);
                        break;
                }
                return true;
            }
        });

        StatusesAPI statusesAPI = new StatusesAPI(MainActivity.token);
        statusesAPI.publicTimeline(20, 1, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                Log.i("Listener", s);
                JSONTokener tokener = new JSONTokener(s);
                try {
                    JSONArray array = (JSONArray) ((JSONObject) tokener.nextValue()).get("statuses");
                    List<Status> statuses = new ArrayList<Status>();
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject object = array.getJSONObject(i);
                        statuses.add(JSONParser.parseStatus(object));
                    }
                    Message message = new Message();
                    message.obj = statuses;
                    message.what = PUBLIC_LINE;
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
                Message message = new Message();
                message.what = WEIBO_ERROR;
                handler.sendMessage(message);
                Log.i("Listener", "error!");
            }
        });
        progressDialog = ProgressDialog.show(this, "",
                "", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActionBar().getThemedContext(), R.array.NavItemList, android.R.layout.simple_list_item_1);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {
                return false;
            }
        });
        return true;
    }
}
