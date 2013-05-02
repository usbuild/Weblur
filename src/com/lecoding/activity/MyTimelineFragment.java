package com.lecoding.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.lecoding.R;
import com.lecoding.data.Status;
import com.lecoding.util.JSONParser;
import com.lecoding.util.WeiboAdapter;
import com.lecoding.view.PullToRefreshListView;
import com.weibo.sdk.android.WeiboException;
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

public class MyTimelineFragment extends Fragment {
    /**
     * Called when the activity is first created.
     */
    private final int WEIBO_ERROR = 0;
    private final int PUBLIC_LINE = 1;
    private final int PAGE_SIZE = 20;
    private final long UP_LOAD = 0;
    private final long DOWN_LOAD = 1;
    Handler handler = null;
    private ListView listView;
    private long sinceId = 0;
    private long maxId = 0;
    private List<Status> oldStatuses = new ArrayList<Status>();
    private WeiboAdapter adapter;


    public void updateListView(List<Status> statuses, int direction) {
        if (statuses.size() > 0) {
            if (direction == 0) {
                if (oldStatuses.size() == 0) maxId = statuses.get(statuses.size() - 1).getId() - 1;
                this.oldStatuses.addAll(0, statuses);
                if (statuses.size() > 0)
                    sinceId = statuses.get(0).getId();
            } else {
                this.oldStatuses.addAll(statuses);
                maxId = statuses.get(statuses.size() - 1).getId() - 1;
            }
            Toast.makeText(getActivity(), "共更新" + statuses.size() + "条微博", Toast.LENGTH_SHORT).show();

            adapter.notifyDataSetChanged();
            if (direction == 0) {
                listView.setSelection(1 + statuses.size());
            } else {
                listView.setSelection(1 + oldStatuses.size() - statuses.size());
            }
        }
    }

    public void loadData() {
        ((PullToRefreshListView) listView).prepareForRefresh();
        new GetDataTask().execute(sinceId, 0L, UP_LOAD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ground, container, false);
        listView = (ListView) view.findViewById(R.id.ground_list);


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Status status = (Status) listView.getAdapter().getItem(pos);
                Intent intent = new Intent(MyTimelineFragment.this.getActivity(), ViewWeiboActivity.class);
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });


        ((PullToRefreshListView) listView).setLoadMore(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetDataTask().execute(0L, maxId, DOWN_LOAD);
            }
        });
        ((PullToRefreshListView) listView).setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDataTask().execute(sinceId, 0L, UP_LOAD);
            }
        });
        if (BaseActivity.token != null) loadData();

        return view;
    }

    private class GetDataTask extends AsyncTask<Long, Void, String[]> {

        @Override
        protected String[] doInBackground(Long... integers) {

            StatusesAPI statusesAPI = new StatusesAPI(BaseActivity.token);
            final long type = integers[2];
            statusesAPI.userTimeline(integers[0], integers[1], PAGE_SIZE, 1, false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
                @Override
                public void onComplete(String s) {
                    JSONTokener tokener = new JSONTokener(s);
                    try {
                        JSONArray array = (JSONArray) ((JSONObject) tokener.nextValue()).get("statuses");
                        List<com.lecoding.data.Status> statuses = new ArrayList<com.lecoding.data.Status>();
                        for (int i = 0; i < array.length(); ++i) {
                            JSONObject object = array.getJSONObject(i);
                            statuses.add(JSONParser.parseStatus(object));
                        }

                        Message message = new Message();
                        message.obj = statuses;
                        if (type == UP_LOAD)
                            message.arg1 = 0;
                        else message.arg1 = 1;
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
                }
            });
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new WeiboAdapter(this.getActivity(), oldStatuses);

        handler = new Handler(new Handler.Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case WEIBO_ERROR:
                        Toast.makeText(MyTimelineFragment.this.getActivity(), "拉取微博信息出错！", Toast.LENGTH_LONG).show();
                        break;
                    case PUBLIC_LINE:
                        updateListView((List<Status>) message.obj, message.arg1);
                        break;
                }
                ((PullToRefreshListView) listView).onRefreshComplete();
                return true;
            }
        });

    }


}
