package com.lecoding.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.lecoding.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-17 下午4:51
 */
public class BaseActivity extends Activity {
    private static final String ACCESS_TOKEN = "2.008NuYtBnYdDWDd4866062744RXaVC";
    private static final String EXPIRES_IN = "157516713";
    public static Oauth2AccessToken token = null;
    private static final String APP_KEY = "3222108305";
    private static final String REDIRECT_URI = "http://usbuild.duapp.com/weiblur.php";
    protected Weibo weibo;
    public static BaseActivity activity;

    private Fragment groundFragment;
    private Fragment timelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (token == null) {
            token = new Oauth2AccessToken(ACCESS_TOKEN, EXPIRES_IN);
            weibo = Weibo.getInstance(APP_KEY, REDIRECT_URI);
        }
        activity = this;

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        setContentView(R.layout.main);
        groundFragment = new GroundFragment();
        timelineFragment = new TimelineFragment();
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
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                switch (i) {
                    case 0:
                        fragmentTransaction.replace(R.id.mainframe, groundFragment);
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.mainframe, timelineFragment);
                        break;
                }
                fragmentTransaction.commit();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login:
                weibo.authorize(this, new WeiblurAuthListener());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
