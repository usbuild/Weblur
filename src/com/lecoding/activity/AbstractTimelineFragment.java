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

/**
 * Created by usbuild on 13-6-17.
 */
public class AbstractTimelineFragment extends Fragment {

    /**
     * Called when the activity is first created.
     */
    protected final int WEIBO_ERROR = 0;
    protected final int PUBLIC_LINE = 1;
    protected final int PAGE_SIZE = 20;
    protected final long UP_LOAD = 0;
    protected final long DOWN_LOAD = 1;
    Handler handler = null;
    protected PullToRefreshListView listView;
    protected long sinceId = 0;
    protected long maxId = 0;
    protected List<Status> oldStatuses = new ArrayList<Status>();
    protected WeiboAdapter adapter;

    private Class<? extends AsyncTask<Long, Void, String[]> > asyncTask;

    public void setAsyncTask(Class<? extends AsyncTask<Long, Void, String[]>> asyncTask) {
        this.asyncTask = asyncTask;
    }

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
                listView.setSelectionFromTop(1 + statuses.size(), 0);
            } else {
                listView.setSelectionFromTop(1 + oldStatuses.size() - statuses.size(), 0);
            }
        }
    }

    public void loadData() {
        listView.prepareForRefresh();
        try {
            asyncTask.newInstance().execute(sinceId, 0L, UP_LOAD);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ground, container, false);
        listView = (PullToRefreshListView) view.findViewById(R.id.ground_list);


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Status status = (Status) listView.getAdapter().getItem(pos);
                Intent intent = new Intent(getActivity(), ViewWeiboActivity.class);
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });


        listView.setLoadMore(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    asyncTask.newInstance().execute(0L, maxId, DOWN_LOAD);
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    asyncTask.newInstance().execute(sinceId, 0L, UP_LOAD);
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        if (BaseActivity.token != null) loadData();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new WeiboAdapter(this.getActivity(), oldStatuses);

        handler = new Handler(new Handler.Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean handleMessage(Message message) {
                listView.onRefreshComplete();
                switch (message.what) {
                    case WEIBO_ERROR:
                        Toast.makeText(getActivity(), "拉取微博信息出错！", Toast.LENGTH_LONG).show();
                        break;
                    case PUBLIC_LINE:
                        updateListView((List<Status>) message.obj, message.arg1);
                        break;
                }
                return true;
            }
        });

    }

}
