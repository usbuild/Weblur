package com.lecoding.activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lecoding.R;
import com.lecoding.data.KeywordProvider;
import com.lecoding.util.KeywordUtil;

/**
 * Created by usbuild on 13-6-17.
 */
public class BlockActivity extends SherlockActivity {

    private ListView listView;
    private CursorAdapter adapter;
    Handler handler;
    KeywordUtil keywordUtil;
    SharedPreferences preferences;
    private final static String BLOCK_KEY = "block_keyword";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("屏蔽设置");

        listView = (ListView) findViewById(R.id.listView);
        registerForContextMenu(listView);

        View view = View.inflate(this, R.layout.block_header, null);

        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(BLOCK_KEY, true);
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        final TextView text = (TextView) view.findViewById(R.id.text);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean(BLOCK_KEY, b).commit();
                if (b) {
                    text.setTextColor(getResources().getColor(R.color.listitem_black));
                } else {
                    text.setTextColor(getResources().getColor(R.color.listitem_gray));
                }
            }
        });
        checkbox.setChecked(isChecked);

        listView.addHeaderView(view);


        Cursor cursor = getContentResolver().query(KeywordProvider.CONTENT_URI, null, null, null, null);
        adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.simple_list_item_1, cursor,
                new String[]{KeywordProvider.NAME}, new int[]{R.id.text1});
        listView.setAdapter(adapter);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Cursor cursor = getContentResolver().query(KeywordProvider.CONTENT_URI, null, null, null, null);
                adapter.changeCursor(cursor);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        keywordUtil = new KeywordUtil(this);

        getContentResolver().registerContentObserver(KeywordProvider.CONTENT_URI, true, new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                handler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menu_add:
                showEditDialog(0, "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.block, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.block_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        Cursor data = (Cursor) adapter.getItem(position);
        String keyword = data.getString(data.getColumnIndex(KeywordProvider.NAME));
        int id = data.getInt(data.getColumnIndex(KeywordProvider._ID));
        switch (item.getItemId()) {
            case R.id.menu_edit:
                showEditDialog(id, keyword);
                break;
            case R.id.menu_remove:
                getContentResolver().delete(ContentUris.withAppendedId(KeywordProvider.CONTENT_URI, id), null, null);
                break;
        }
        return true;
    }

    public void showEditDialog(final int id, final String str) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("设置关键字");

        final EditText input = new EditText(this);
        input.setText(str);
        alert.setView(input);

        alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String text = input.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getApplicationContext(), "输入的内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (keywordUtil.hasKey(text)) {
                    Toast.makeText(getApplicationContext(), "已包含该关键词", Toast.LENGTH_LONG).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(KeywordProvider.NAME, text);
                if (TextUtils.isEmpty(str)) {
                    getContentResolver().insert(KeywordProvider.CONTENT_URI, values);
                } else {
                    getContentResolver().update(ContentUris.withAppendedId(KeywordProvider.CONTENT_URI, id), values, null, null);
                }
                adapter.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}