package com.lecoding.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.lecoding.R;
import com.lecoding.data.User;
import com.weibo.sdk.android.*;
import com.weibo.sdk.android.api.AccountAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-17 下午4:51
 */
public class BaseActivity extends SherlockFragmentActivity {
    // for test, not use
    private static final String ACCESS_TOKEN = "2.008NuYtBnYdDWDd4866062744RXaVC";
    private static final String EXPIRES_IN = "157516713";

    public static Oauth2AccessToken token = null;
    private static final String APP_KEY = "3222108305";
    private static final String REDIRECT_URI = "http://usbuild.duapp.com/weiblur.php";
    protected Weibo weibo;
    public static BaseActivity activity;

    public static long uid;

    private GroundFragment groundFragment;
    private TimelineFragment timelineFragment;
    private MyTimelineFragment myTimelineFragment;

    private Handler handler;

    private final int WB_SUCCESS = 0;
    private final int WB_EXCPETION = 1;
    private final int WB_ERROR = 2;
    private final int WB_CANCEL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        setContentView(R.layout.main);

        groundFragment = new GroundFragment();
        timelineFragment = new TimelineFragment();
        myTimelineFragment = new MyTimelineFragment();
//        token = new Oauth2AccessToken(ACCESS_TOKEN, EXPIRES_IN);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case WB_SUCCESS:
                        BaseActivity.token = (Oauth2AccessToken) message.obj;

                        AccountAPI accountAPI = new AccountAPI(token);
                        accountAPI.getUid(new RequestListener() {
                            @Override
                            public void onComplete(String response) {
                                uid = Long.parseLong(response.substring(7, response.length() - 1).trim());
                            }

                            @Override
                            public void onIOException(IOException e) {

                            }

                            @Override
                            public void onError(WeiboException e) {

                            }
                        });
                        timelineFragment.loadData();
                        break;
                    case WB_EXCPETION:
                        Toast.makeText(BaseActivity.this, "授权出错", Toast.LENGTH_LONG);
                        finish();
                        break;
                    case WB_ERROR:
                        showSettingsAlert();
                        break;
                    case WB_CANCEL:
                        Toast.makeText(BaseActivity.this, "授权已取消", Toast.LENGTH_LONG);
                        finish();
                        break;
                }
                return false;
            }
        });

        weibo = Weibo.getInstance(APP_KEY, REDIRECT_URI);
        getToken();
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("设置网络");

        // Setting Dialog Message
        alertDialog.setMessage("网络尚未打开，您希望设置么？");

        alertDialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivityForResult(intent, 0);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        alertDialog.show();
    }

    public boolean isNetworkAvailable() {
        Context context = getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (info != null) {//逐一查找状态为已连接的网络
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getToken() {
        if (!isNetworkAvailable()) {
            showSettingsAlert();
            return;
        }
        if (token == null) {
            weibo.authorize(this, new WeiboAuthListener() {
                @Override
                public void onComplete(Bundle bundle) {
                    String token = bundle.getString("access_token");
                    String expiresIn = bundle.getString("expires_in");
                    Message message = new Message();
                    message.what = WB_SUCCESS;
                    message.obj = new Oauth2AccessToken(token, expiresIn);
                    handler.sendMessage(message);
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    Message message = new Message();
                    message.what = WB_EXCPETION;
                    message.obj = e.toString();
                    handler.sendMessage(message);
                }

                @Override
                public void onError(WeiboDialogError weiboDialogError) {
                    Message message = new Message();
                    message.what = WB_ERROR;
                    message.obj = weiboDialogError.getMessage();
                    handler.sendMessage(message);
                }

                @Override
                public void onCancel() {
                    Message message = new Message();
                    message.what = WB_CANCEL;
                    message.obj = "Auth Canceled";
                    handler.sendMessage(message);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_activity, menu);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(), R.array.NavItemList, android.R.layout.simple_list_item_1);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case 0:
                        fragmentTransaction.replace(R.id.mainframe, groundFragment);
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.mainframe, timelineFragment);
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.mainframe, myTimelineFragment);
                }
                fragmentTransaction.commit();
                return false;
            }
        });
        getSupportActionBar().setSelectedNavigationItem(1);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login:
                Intent intent1 = new Intent(this, SettingActivity.class);
                startActivity(intent1);
                return true;
            case R.id.menu_post:
                Intent intent = new Intent(this, PostActivity.class);
                intent.putExtra("type", PostActivity.STATUS);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
