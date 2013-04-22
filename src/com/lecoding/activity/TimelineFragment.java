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

public class TimelineFragment extends Fragment {
    /**
     * Called when the activity is first created.
     */
    private final int WEIBO_ERROR = 0;
    private final int PUBLIC_LINE = 1;
    Handler handler = null;
    private ListView listView;
    private long sinceId = 0;
    private List<Status> oldStatuses;
    private WeiboAdapter adapter;

    public void updateListView(List<Status> statuses) {
        this.oldStatuses.addAll(0, statuses);
        Toast.makeText(getActivity(), "共更新" + statuses.size() + "条微博", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ground, container, false);
        listView = (ListView) view.findViewById(R.id.ground_list);
        oldStatuses = new ArrayList<Status>();

        adapter = new WeiboAdapter(this.getActivity(), oldStatuses);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Status status = (Status) listView.getAdapter().getItem(pos);
                Intent intent = new Intent(TimelineFragment.this.getActivity(), ViewWeiboActivity.class);
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });

        ((PullToRefreshListView) listView).setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDataTask().execute();
            }
        });
        ((PullToRefreshListView) listView).prepareForRefresh();
        new GetDataTask().execute();
        return view;
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {

            StatusesAPI statusesAPI = new StatusesAPI(BaseActivity.token);
            statusesAPI.homeTimeline(sinceId, 0, 30, 1, false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
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
                        message.what = PUBLIC_LINE;
                        sinceId = statuses.get(0).getId();
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

        handler = new Handler(new Handler.Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case WEIBO_ERROR:
                        Toast.makeText(TimelineFragment.this.getActivity(), "拉取微博信息出错！", Toast.LENGTH_LONG).show();
                        break;
                    case PUBLIC_LINE:
                        updateListView((List<Status>) message.obj);
                        break;
                }
                ((PullToRefreshListView) listView).onRefreshComplete();
                return true;
            }
        });

    }


}
