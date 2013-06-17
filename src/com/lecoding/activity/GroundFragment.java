package com.lecoding.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;

public class GroundFragment extends AbstractTimelineFragment {
    @Override
    protected void loadUp() {
        new StatusesAPI(BaseActivity.token).publicTimeline(PAGE_SIZE, 1, false, new RequestListener() {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        listView.getmLoadMore().setVisibility(View.GONE);
        return view;
    }
}
