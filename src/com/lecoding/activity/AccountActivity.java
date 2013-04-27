package com.lecoding.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.weibo.sdk.android.api.SearchAPI;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-24 下午7:55
 */
public class AccountActivity extends SherlockActivity {

    private EditText searchContent;
    private Button searchBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchBtn = (Button) findViewById(R.id.search_btn);
        searchContent = (EditText) findViewById(R.id.search_content);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = searchContent.getText().toString();
                SearchAPI searchAPI = new SearchAPI(BaseActivity.token);
            }
        });

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