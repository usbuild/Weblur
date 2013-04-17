package com.lecoding.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.lecoding.R;
import com.lecoding.data.Status;
import com.lecoding.util.JSONParser;
import com.lecoding.util.WeiboAdapter;
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

public class GroundFragment extends Fragment {
    /**
     * Called when the activity is first created.
     */
    private final int WEIBO_ERROR = 0;
    private final int PUBLIC_LINE = 1;
    Handler handler = null;
    private ListView listView;
    private ProgressDialog progressDialog;

    public void updateListView(List<Status> statuses) {
        listView.setAdapter(new WeiboAdapter(this.getActivity(), statuses));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gound, container, false);
        listView = (ListView) view.findViewById(R.id.ground_list);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(new Handler.Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean handleMessage(Message message) {
                progressDialog.cancel();
                switch (message.what) {
                    case WEIBO_ERROR:
                        Toast.makeText(GroundFragment.this.getActivity(), "拉取微博信息出错！", Toast.LENGTH_LONG).show();
                        break;
                    case PUBLIC_LINE:
                        updateListView((List<Status>) message.obj);
                        break;
                }
                return true;
            }
        });

        StatusesAPI statusesAPI = new StatusesAPI(BaseActivity.token);
        statusesAPI.publicTimeline(20, 1, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
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
            }
        });
        progressDialog = ProgressDialog.show(getActivity(), "",
                "正在拉取数据", true);
    }

}
