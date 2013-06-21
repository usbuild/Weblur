package com.lecoding.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.data.User;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.SearchAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;

/**
 * Created by usbuild on 13-5-25.
 */
public class WeiboListActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);

        User user = (User) getIntent().getSerializableExtra("user");
        setContentView(R.layout.main);
        getSupportActionBar().setTitle(user.getScreenName() + "的微博");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainframe, new WeiboListFragment(user.getId()));
        fragmentTransaction.commit();
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

class WeiboListFragment extends AbstractTimelineFragment {
    WeiboListFragment(long uid) {
        this.uid = uid;
    }

    private long uid;


    @Override
    protected void loadUp() {
        new StatusesAPI(BaseActivity.token).userTimeline(uid, sinceId, maxId, PAGE_SIZE, 1, false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                setUp(s);
            }

            @Override
            public void onIOException(IOException e) {
                loadError();
            }

            @Override
            public void onError(WeiboException e) {
                loadError();
            }
        });
    }

    @Override
    protected void loadDown() {
        new StatusesAPI(BaseActivity.token).userTimeline(uid, 0, maxId, PAGE_SIZE, 1, false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                setDown(response);
            }

            @Override
            public void onIOException(IOException e) {
                loadError();
            }

            @Override
            public void onError(WeiboException e) {
                loadError();
            }
        });
    }
}