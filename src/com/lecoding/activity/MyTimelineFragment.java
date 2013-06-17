package com.lecoding.activity;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;

public class MyTimelineFragment extends AbstractTimelineFragment {
    @Override
    protected void loadUp() {
        new StatusesAPI(BaseActivity.token).userTimeline(sinceId, 0, PAGE_SIZE, 1, false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                setUp(response);
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
        new StatusesAPI(BaseActivity.token).userTimeline(0, maxId, PAGE_SIZE, 1, false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
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
